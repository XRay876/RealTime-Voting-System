export const setAuth = (data) => {
    localStorage.setItem("accessToken", data.accessToken)
    localStorage.setItem("refreshToken", data.refreshToken)
}

export const getToken = () => localStorage.getItem("accessToken")

export const logout = () => {
    localStorage.clear()
}