const API_BASE_URL = '/api';

const auth = {
    setToken: (token) => sessionStorage.setItem('jwt_token', token),
    getToken: () => sessionStorage.getItem('jwt_token'),
    logout: () => {
        sessionStorage.removeItem('jwt_token');
        sessionStorage.removeItem('user_data');
    },
    isAuthenticated: () => !!sessionStorage.getItem('jwt_token'),
    getUser: () => JSON.parse(sessionStorage.getItem('user_data')),
    setUser: (user) => sessionStorage.setItem('user_data', JSON.stringify(user)),
    getRoles: () => JSON.parse(sessionStorage.getItem('user_data'))?.roles || [],
    hasRole: (role) => (JSON.parse(sessionStorage.getItem('user_data'))?.roles || []).includes(role)
};

async function apiRequest(endpoint, options = {}) {
    const token = auth.getToken();
    const headers = {
        'Content-Type': 'application/json',
        ...options.headers
    };

    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
        ...options,
        headers
    });

    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || 'Something went wrong');
    }

    // Some endpoints return strings, check if response is JSON
    const contentType = response.headers.get("content-type");
    if (contentType && contentType.indexOf("application/json") !== -1) {
        return response.json();
    } else {
        return response.text();
    }
}

// UI Utilities
function showNotification(message, type = 'success') {
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.textContent = message;
    document.body.appendChild(toast);
    setTimeout(() => toast.remove(), 3000);
}

// Export functions to window if needed
window.auth = auth;
window.apiRequest = apiRequest;
window.showNotification = showNotification;
