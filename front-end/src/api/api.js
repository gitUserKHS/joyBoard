import axios from "axios";

export const API_SERVER_HOST = "http://localhost:8080";

export const boardApiPrefix = API_SERVER_HOST + "/api/board";

export const getOne = async (postId) => {
    const res = await axios.get(`${boardApiPrefix}/post/${postId}`);
    return res.data;
}

export const getList = async (page) => {
    const res = await axios.get(`${boardApiPrefix}/list?page=${page}`);
    return res.data;
}