// https://api-ninjas.com/api/geocoding
// https://geocoding-api.open-meteo.com/v1/search?name=Christchurch

const GEOLOCATION_URL = "https://nominatim.openstreetmap.org/search"

// https://nominatim.org/release-docs/develop/api/Search/
// q - Free-form query string to search for
function findLatitudeLongtitudeFromName(locationName) {
    const url = "https://nominatim.openstreetmap.org/search";
    const params = {
        q: locationName,
        format: "json",
        limit: 1,
    };
    return axios
        .get(url, {
            params
        })
        .then((response) => {
            const data = response.data;
            if (data.length > 0) {
                const {
                    lat,
                    lon
                } = data[0];
                return {
                    latitude: parseFloat(lat),
                    longitude: parseFloat(lon),
                };
            } else {
                throw new Error("Location not found");
            }
        })
        .catch((error) => {
            console.error("Geocoding error:", error);
            return null;
        });
}