const CACHE_NAME = 'hype-barber-v1';

// Fișierele statice pe care vrem să le păstrăm în telefon pentru viteză
// NU punem aici pagini dinamice (dashboard), doar CSS, JS, logo-uri.
const ASSETS_TO_CACHE = [
    '/css/styles.css',
    '/icons/icon-192x192.png',
    'https://fonts.googleapis.com/css2?family=Inter:wght@300;400;600&family=Playfair+Display:wght@700&display=swap'
];

// 1. Install Event: Salvăm resursele statice în cache
self.addEventListener('install', (event) => {
    event.waitUntil(
        caches.open(CACHE_NAME).then((cache) => {
            console.log('[Service Worker] Caching static assets');
            return cache.addAll(ASSETS_TO_CACHE);
        })
    );
});

// 2. Fetch Event: Interceptăm cererile
self.addEventListener('fetch', (event) => {
    // Strategie: Network First (Pentru Dashboard vrem mereu date proaspete!)
    // Încearcă să ia de pe net. Dacă nu are net, abia atunci caută în cache (pentru CSS/imagini).
    event.respondWith(
        fetch(event.request).catch(() => {
            return caches.match(event.request);
        })
    );
});