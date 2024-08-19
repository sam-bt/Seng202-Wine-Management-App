let map, markersLayer, heatMapLayer;
const markers = [];
let heatMapData = [];

const jsConnector = {
    initMap: initMap,
};

function initMap(format, data) {
    map = new L.map('map', MAP_CONFIG);
    new L.TileLayer(TILE_SERVER_URL, {
        attribution: TILE_SERVER_ATTRIBUTION
    }).addTo(map)

    // Marker Clusters - Combines nearby markers into 1
    markersLayer = L.markerClusterGroup();
    map.addLayer(markersLayer);

    // Heatmap
    heatMapLayer = new HeatmapOverlay(HEATMAP_CONFIG);
    map.addLayer(heatMapLayer);

    initTestData();

    console.log(heatMapData);
    heatMapLayer.setData({data: heatMapData}, data);
}

function initTestData() {
    TEST_DATA.forEach((location) => {
        let marker = L.marker([location.lat, location.lon]);
        markersLayer.addLayer(marker);

        heatMapData.push({lat: location.lat, lon: location.lon})
    })
}
