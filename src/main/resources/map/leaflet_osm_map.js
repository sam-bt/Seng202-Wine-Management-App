let map;

let jsConnector = {
    initMap: initMap,
}

function initMap() {
    let mapOptions = {
        center: [-44.0, 171.0],
        zoom: 6
    }
    map = new L.map('map', mapOptions);
    new L.TileLayer('https://tile.csse.canterbury.ac.nz/hot/{z}/{x}/{y}.png', {
        attribution: '© OpenStreetMap contributors<br>Served by University of Canterbury'
    }).addTo(map)
}
