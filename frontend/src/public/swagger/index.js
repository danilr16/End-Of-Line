import { useState, useEffect } from "react";
import SwaggerUI from "swagger-ui-react";
import "swagger-ui-react/swagger-ui.css"

export default function SwaggerDocs(){
    const [docs,setDocs]=useState({});
    useEffect(() =>{loadDocs();},[]);

    async function loadDocs() {
        const mydocs = await (await fetch(`/v3/api-docs`, {
            headers: {
                "Content-Type": "application/json",
            },
        })).json();
        setDocs(mydocs);
    }

    useEffect(() => {
        // Set background color of the entire page when the component is mounted
        document.body.style.backgroundColor = 'white';
        
        // Cleanup: reset background when component is unmounted
        return () => {
          document.body.style.backgroundColor = '';
        };
      }, []); // Empty dependency array to run once on mount
    
      return (
        <SwaggerUI spec={docs} url="" />
    );
    
}