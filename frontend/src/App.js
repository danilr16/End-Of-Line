import React from "react";
import { Route, Routes } from "react-router-dom";
import jwt_decode from "jwt-decode";
import { ErrorBoundary } from "react-error-boundary";
import AppNavbar from "./AppNavbar";
import Home from "./home";
import PrivateRoute from "./privateRoute";
import Register from "./auth/register";
import Login from "./auth/login";
import Logout from "./auth/logout";
import PlanList from "./public/plan";
import tokenService from "./services/token.service";
import UserListAdmin from "./admin/users/UserListAdmin";
import UserEditAdmin from "./admin/users/UserEditAdmin";
import GameListAdmin from "./admin/games/GameListAdmin";
import SwaggerDocs from "./public/swagger";
import Rules from './screens/Rules';
import CurrentGames from "./screens/CurrentGames";
import MyGames from "./screens/MyGames";
import Profile from './screens/Profile';
import GameScreen from "./screens/GameScreen";
import { ColorProvider } from "./ColorContext";

function ErrorFallback({ error, resetErrorBoundary }) {
  return (
    <div role="alert">
      <p>Something went wrong:</p>
      <pre>{error.message}</pre>
      <button onClick={resetErrorBoundary}>Try again</button>
    </div>
  )
}

function App() {
  const jwt = tokenService.getLocalAccessToken();
  let roles = []
  if (jwt) {
    roles = getRolesFromJWT(jwt);
  }

  function getRolesFromJWT(jwt) {
    return jwt_decode(jwt).authorities;
  }

  let adminRoutes = <></>;
  let ownerRoutes = <></>;
  let userRoutes = <></>;
  let vetRoutes = <></>;
  let publicRoutes = <></>;

  roles.forEach((role) => {
    if (role === "ADMIN") {
      adminRoutes = (
        <>
          <Route path="/users" exact={true} element={<PrivateRoute><UserListAdmin /></PrivateRoute>} />
          <Route path="/users/:username" exact={true} element={<PrivateRoute><UserEditAdmin /></PrivateRoute>} />
          <Route path="/games" exact={true} element={<PrivateRoute><GameListAdmin /></PrivateRoute>} />          
        </>)
    }
    if (role === "PLAYER") {
      ownerRoutes = (
        <>
        </>)
    }    
  })
  if (!jwt) {
    publicRoutes = (
      <>        
        <Route path="/register" element={<Register />} />
        <Route path="/login" element={<Login />} />
        <Route path="/rules" element={<Rules />} />
        <Route path="/test" element={<MyGames />} />
        <Route path="/game/:gameCode" exact={true} element={<PrivateRoute><GameScreen /></PrivateRoute>} />  
        
      </>
    )
  } else {
    userRoutes = (
      <>
        {/* <Route path="/dashboard" element={<PrivateRoute><Dashboard /></PrivateRoute>} /> */}        
        <Route path="/logout" element={<Logout />} />
        <Route path="/login" element={<Login />} />
        <Route path="/rules" element={<Rules />} />
        <Route path="/profile" element={<Profile />} />
        <Route path="/games/current" element={<CurrentGames />} />
        <Route path="/users/games" exact={true} element={<PrivateRoute><MyGames /></PrivateRoute>} />
        <Route path="/game/:gameCode" exact={true} element={<PrivateRoute><GameScreen /></PrivateRoute>} />  
      </>
  )
  }

  return (
    <ColorProvider>
      <div>
        <ErrorBoundary FallbackComponent={ErrorFallback} >
          <AppNavbar />
          <Routes>
            <Route path="/" exact={true} element={<Home />} />
            <Route path="/plans" element={<PlanList />} />
            <Route path="/docs" element={<SwaggerDocs />} />
            <Route path="/rules" element={<Rules />} />
            {publicRoutes}
            {userRoutes}
            {adminRoutes}
            {ownerRoutes}
            {vetRoutes}
          </Routes>
        </ErrorBoundary>
      </div>
    </ColorProvider>
  );
}

export default App;
