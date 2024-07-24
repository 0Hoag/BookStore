import httpClient from "../configurations/httpClient";
import { API } from "../configurations/configuration";
import { getToken } from "./localStorageService";

const profileService = {

    updateProfile: async(profileId, newProfile) => {
        try {
            const token = getToken();
            const url = `${API.UPDATE_PROFILE}/${profileId}`;
            const response = httpClient.put(url, newProfile, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            return response.data.result;
        }catch (error) {
            throw error;
        }
    },


}

export default profileService;