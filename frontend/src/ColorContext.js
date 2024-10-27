import React, { createContext, useContext, useState, useEffect } from 'react';

const ColorContext = createContext();

export const ColorProvider = ({ children }) => {
    const [colors, setColors] = useState({
        light: 'var(--br-yellow-light)',  // Light color
        normal: 'var(--br-yellow)', // Normal color
        dark: 'var(--br-yellow-dark)',    // Dark color
    });

    const updateColors = (newColors) => {
        setColors((prevColors) => ({
            ...prevColors,
            ...newColors
        }));
    };

    useEffect(() => {
        document.documentElement.style.setProperty('--br-c-light', colors.light);
        document.documentElement.style.setProperty('--br-c-normal', colors.normal);
        document.documentElement.style.setProperty('--br-c-dark', colors.dark);
    }, [colors]); 

    return (
        <ColorContext.Provider value={{ colors, updateColors }}>
            {children}
        </ColorContext.Provider>
    );
};

export const useColors = () => {
    return useContext(ColorContext);
};