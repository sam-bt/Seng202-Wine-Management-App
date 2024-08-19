// https://api-ninjas.com/api/geocoding
// https://geocoding-api.open-meteo.com/v1/search?name=Christchurch

const GEOLOCATION_URL = "https://geocoding-api.open-meteo.com/v1/search"

function findCoordinatesFromLocation(location, callback) {
    const request = new Request(GEOLOCATION_URL, {
        method: "POST",
        body: { name: location,
            count: 1,
            language: "en",
            format: "json" },
    });

    fetch(request)
        .then((response) => console.log(response))
        .catch((err) => console.log(err));
}