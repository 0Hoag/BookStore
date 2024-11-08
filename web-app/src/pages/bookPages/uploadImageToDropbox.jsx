const fetch = require("node-fetch");

const uploadImageToDropbox = async (file) => {
    try {
        // Replace ACCESS_TOKEN with your Dropbox app's access token
        const ACCESS_TOKEN = "sl.B6MEsh5JIAOMpT-5CeasFfmi7S8XVKyrVKnMm5JIgabHa9bvAXpRgXpwEKf2IfpBVbw8gm0HLs1kFVkXtZjD9DGiMp3_ZLyUken8ucZ2AlhW74g3l70BnnBFS50rAujWEd7TSwAOswDc57k";

        // Step 1: Upload the file to Dropbox
        const uploadResponse = await fetch("https://content.dropboxapi.com/2/files/upload", {
            method: "POST",
            headers: {
                Authorization: `Bearer ${ACCESS_TOKEN}`,
                "Content-Type": "application/octet-stream",
                "Dropbox-API-Arg": JSON.stringify({
                    path: `/book_images/${file.name}`,
                    mode: "add",
                    autorename: true,
                    mute: false,
                }),
            },
            body: file,
        });

        if (!uploadResponse.ok) {
            throw new Error("Failed to upload image to Dropbox");
        }

        const uploadData = await uploadResponse.json();
        const { path_display } = uploadData;

        // Step 2: Create a shared link for the uploaded file
        const createLinkResponse = await fetch("https://api.dropboxapi.com/2/sharing/create_shared_link_with_settings", {
            method: "POST",
            headers: {
                Authorization: `Bearer ${ACCESS_TOKEN}`,
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                path: path_display,
                settings: {
                    requested_visibility: "public",
                },
            }),
        });

        if (!createLinkResponse.ok) {
            throw new Error("Failed to create shared link for the image");
        }

        const linkData = await createLinkResponse.json();
        const publicURL = linkData.url.replace("?dl=0", "?raw=1"); // Convert the link to direct image URL

        return { publicURL, path_display };
    } catch (error) {
        console.error("Error uploading image to Dropbox:", error);
        throw error;
    }
};

module.exports = uploadImageToDropbox;
