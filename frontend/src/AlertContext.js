import React, { createContext, useContext, useState } from 'react';

const AlertContext = createContext();

export const AlertProvider = ({children}) => {
const defaultMessage = "";
const [alertMessage, setAlertMessage] = useState(defaultMessage);

const updateAlert = (newAlert) =>{
    setAlertMessage(newAlert);
};

return (
    <AlertContext.Provider value={{alertMessage,updateAlert}}>
        {children}
    </AlertContext.Provider>
);
}

export const useAlert = () =>{
    return useContext(AlertContext);
}