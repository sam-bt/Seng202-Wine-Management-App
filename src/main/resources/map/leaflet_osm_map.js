let map, markersLayer, heatMapLayer;
const markers = [];
let heatMapData = [];

const jsConnector = {
    initMap: initMap,
};

function initMap() {
    map = new L.map('map', MAP_CONFIG);
    new L.TileLayer(TILE_SERVER_URL, {
        attribution: TILE_SERVER_ATTRIBUTION
    }).addTo(map)

    // Marker Clusters - Combines nearby markers into 1
    markersLayer = L.markerClusterGroup({
        iconCreateFunction: function (cluster) {
            return L.divIcon({
                html: `<div><img src="wine_glass_small.png"><span>${cluster.getChildCount()}</span></div>`,
                className: "custom-cluster-icon",
                iconSize: L.point(36, 60),
            });
        },
        // maxClusterRadius: 100,
    });
    map.addLayer(markersLayer);

    // Heatmap
    heatMapLayer = new HeatmapOverlay(HEATMAP_CONFIG);
    map.addLayer(heatMapLayer);

    initTestData();

    heatMapLayer.setData({data: heatMapData}, TEST_DATA);
}

function addWineMarker(latitude, longitude, locationName = null) {
    const marker = L.marker([latitude, longitude], {
        icon: WINE_ICON,
        riseOnHover: true,
    });
    if (locationName) {
        marker.bindPopup(
            `Name: WineName<br>
            Type: WineType<br>
            Location: ${locationName}`
        );
    }
    markers.push(marker);
    markersLayer.addLayer(marker);
    heatMapData.push({ lat: latitude, lon: longitude });
}

function initTestData() {
    TEST_DATA.forEach((point) => {
        addWineMarker(point.lat, point.lon, point.name);
    });
}
