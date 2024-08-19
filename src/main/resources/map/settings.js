const TILE_SERVER_URL = "https://tile.csse.canterbury.ac.nz/hot/{z}/{x}/{y}.png";
const TILE_SERVER_ATTRIBUTION = "© OpenStreetMap contributors<br>Served by University of Canterbury"

const HEATMAP_CONFIG = {
    radius: 0.5,
    scaleRadius: true,
    maxOpacity: 0.4,
    latField: 'lat',
    lngField: 'lon'
}

const MAP_CONFIG = {
    center: [-44.0, 171.0],
    zoom: 6
}

const WINE_ICON = L.icon({
    iconUrl: "wine_glass_small.png",
    iconSize: [36, 60],
    iconAnchor: [24, 40],
    popupAnchor: [0, -40],
});