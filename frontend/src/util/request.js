
export default async function request(url,method,body,jwt,customHeaders = null){
    const reqHeaders = customHeaders ? customHeaders:{'Content-Type': 'application/json', 'Authorization': `Bearer ${jwt}`}
    const  response =  await fetch(url,
        {
            method: method,
            headers: reqHeaders,
            body : JSON.stringify(body),
        }
    )
    const resContent = await response.json();
    return {response,resContent};
}
