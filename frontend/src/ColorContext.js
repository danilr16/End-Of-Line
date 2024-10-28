import React, { createContext, useContext, useState, useEffect } from 'react';

const ColorContext = createContext();

export const ColorProvider = ({ children }) => {
    const [colors, setColors] = useState({
        light: 'var(--br-yellow-light)',  // Light color
        normal: 'var(--br-yellow)', // Normal color
        dark: 'var(--br-yellow-dark)',    // Dark color
    });

    const updateColors = (newColors) => {
        setColors((prevColors) => {
            const hasChanged = Object.keys(newColors).some(
                (key) => newColors[key] !== prevColors[key]
            );
    
            // Solo actualiza el estado si los colores han cambiado
            return hasChanged ? { ...prevColors, ...newColors } : prevColors;
        });
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