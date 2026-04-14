import axios from "axios"

const API = axios.create({
    baseURL: "http://localhost:8080/api"
})

API.interceptors.request.use((config) => {
    const token = localStorage.getItem("accessToken")
    if (token) {
        config.headers.Authorization = `Bearer ${token}`
    }
    return config
})

// API.interceptors.response.use(
//     (response) => response,
//     async (error) => {
//         if (error.response?.status === 401) {
//             //refresh flow
//         }
//         return Promise.reject(error)
//     }
// )

export default API