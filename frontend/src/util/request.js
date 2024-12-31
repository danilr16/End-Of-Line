
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
    const  response =  await fetch(url,data)

    if (response.status !== 204) {
        const resContent = await response.json();
        return { response, resContent };
    }
    return { response };

}
