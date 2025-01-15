
export default async function request(url,method,body=null,jwt=null,customHeaders = null){
    const reqHeaders = customHeaders ? customHeaders:{'Content-Type': 'application/json', 'Authorization': `Bearer ${jwt}`}
    const data = body?{
        method: method,
        headers: reqHeaders,
        body : JSON.stringify(body),
    }:{
        method: method,
        headers:reqHeaders
    }
    try {
        const response = await fetch(url, data);

        // Verifica si la respuesta no es exitosa
        if (!response.ok) {
            const errorContent = await response.json();
            console.error(`HTTP error! status: ${response.status}, message: ${errorContent.message || response.statusText}`);
            return { error: new Error(`HTTP error! status: ${response.status}, message: ${errorContent.message || response.statusText}`) };
        }

        // Maneja la respuesta cuando no hay contenido (204 No Content)
        if (response.status === 204) {
            return { response };
        }

        // Maneja la respuesta cuando hay contenido
        const resContent = await response.json();
        return { response, resContent };
    } catch (error) {
        console.error("Error fetching data:", error);
        return { error };
    }

}


