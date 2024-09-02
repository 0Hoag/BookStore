import httpClient from "../configurations/httpClient";
import { API } from "../configurations/configuration";
import { getToken } from "./localStorageService";

const profileService = {
    createProfile: async (data) => {
        const url = API.CREATE_PROFILE;
        const response = httpClient.post(url, data);
        return response;
    },
}

export default profileService;