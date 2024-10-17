import axios from 'axios';
import tokenService from '../services/token.service';

const API_URL = '/api/v1/users/current';

const getCurrentUser = async () => {
    try {
        const token = tokenService.getLocalAccessToken();
        const response = await axios.get(API_URL, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
        return response.data; 
    } catch (error) {
        console.error("Error fetching current user", error);
        throw error; 
    }
};

export default {
    getCurrentUser,
};