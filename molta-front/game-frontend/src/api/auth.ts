import axiosInstance from './axiosConfig';

export const registerUser = async (userId: string, name: String, password: string, nickname: string) => {
    return axiosInstance.post('/user/register', {
        userId,
        name,
        password,
        nickname,
    });
};

export const loginUser = async (userId: string, password: string) => {
    try {
        const response = await axiosInstance.post('/user/login', { userId, password });
        console.log("Sending login request with:", { userId, password });
        console.log("res", response);
        return response;
    } catch (error) {
        console.error("Login error:", error);
        throw error;
    }
};