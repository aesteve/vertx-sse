var map = L.map('map').setView([0,0], 2);


function moveISS(data) {
    var lat = data['latitude'];
    var lon = data['longitude'];

    iss.setLatLng([lat, lon]);
    isscirc.setLatLng([lat, lon]);
    map.panTo([lat, lon], animate=true);
}

L.tileLayer('http://open-notify.org/Open-Notify-API/map/tiles/{z}/{x}/{y}.png', {
    maxZoom: 4,
}).addTo(map);

var ISSIcon = L.icon({
    iconUrl: 'http://open-notify.org/Open-Notify-API/map/ISSIcon.png',
    iconSize: [50, 30],
    iconAnchor: [25, 15],
    popupAnchor: [50, 25],
    shadowUrl: 'http://open-notify.org/Open-Notify-API/map/ISSIcon_shadow.png',
    shadowSize: [60, 40],
    shadowAnchor: [30, 15]
});


var iss = L.marker([0, 0], {icon: ISSIcon}).addTo(map);
var isscirc = L.circle([0,0], 2200e3, {color: "#c22", opacity: 0.3, weight:1, fillColor: "#c22", fillOpacity: 0.1}).addTo(map);

var es = new EventSource('/iss/position');
es.onmessage = function(msg) {
    moveISS(JSON.parse(msg.data));
};
