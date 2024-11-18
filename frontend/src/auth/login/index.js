import React, { useState } from "react";
import { Alert } from "reactstrap";
import { useLocation, useNavigate } from "react-router-dom";
import FormGenerator from "../../components/formGenerator/formGenerator";
import tokenService from "../../services/token.service";
import "../../static/css/auth/authButton.css";
import { loginFormInputs } from "./form/loginFormInputs";

export default function Login() {
  const [message, setMessage] = useState(null)
  const loginFormRef = React.createRef();      
  const location = useLocation();
  const navigate = useNavigate();

  const gameCode = location.pathname.startsWith("/game/") ? location.pathname.split("/")[2] : null;


  async function handleSubmit({ values }) {

    const reqBody = values;
    setMessage(null);
    await fetch("/api/v1/auth/signin", {
      headers: { "Content-Type": "application/json" },
      method: "POST",
      body: JSON.stringify(reqBody),
    })
      .then(function (response) {
        if (response.status === 200) return response.json();
        else return Promise.reject("Invalid login attempt");
      })
      .then(function (data) {
        tokenService.setUser(data);
        tokenService.updateLocalAccessToken(data.token);
        if (gameCode) {
          window.location.assign(`/game/${gameCode}`);
        } else {
          navigate("/");
        }
      })

      .catch((error) => {         
        setMessage(error);
      });            
  }

  
    return (
      <div className="auth-page-container">
        {message ? (
          <Alert color="primary">{message}</Alert>
        ) : (
          <></>
        )}

        <h1 style={{color: "#fff"}}>Login</h1>

        <div className="auth-form-container">
          <FormGenerator
            ref={loginFormRef}
            inputs={loginFormInputs}
            onSubmit={handleSubmit}
            numberOfColumns={1}
            listenEnterKey
            buttonText="Login"
            buttonClassName="auth-button"
          />
        </div>
      </div>
    );  
}