import React, { createContext, useContext, useState, useEffect } from 'react';
import { useLocation } from 'react-router-dom';

const ColorContext = createContext();

export const ColorProvider = ({ children }) => {
    const defaultColors = {
        light: 'var(--br-yellow-light)',  // Light color
        normal: 'var(--br-yellow)',       // Normal color
        dark: 'var(--br-yellow-dark)',    // Dark color
    };

    const [colors, setColors] = useState(defaultColors);

    const updateColors = (newColors) => {
        setColors((prevColors) => {
            const hasChanged = Object.keys(newColors).some(
                (key) => newColors[key] !== prevColors[key]
            );
            return hasChanged ? { ...prevColors, ...newColors } : prevColors;
        });
    };

    const location = useLocation(); 

    useEffect(() => {
        const matchGamePath = /^\/game\/\w+$/.test(location.pathname);
        
        if (!matchGamePath) {
            setColors(defaultColors);
        }

        document.documentElement.style.setProperty('--br-c-light', colors.light);
        document.documentElement.style.setProperty('--br-c-normal', colors.normal);
        document.documentElement.style.setProperty('--br-c-dark', colors.dark);
    }, [colors, location.pathname]); 

    return (
        <ColorContext.Provider value={{ colors, updateColors }}>
            {children}
        </ColorContext.Provider>
    );
};

export const useColors = () => {
    return useContext(ColorContext);
};