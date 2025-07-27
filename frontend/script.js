// --- Configuración Global ---
const API_BASE_URL = 'http://localhost:7000/api';
let CURRENT_USER = null;
let USER_TOKEN = null;
let USER_ROLE = null;
let USER_COMPANY_ID = null;

// --- Referencias a Elementos del DOM ---
const pages = document.querySelectorAll('.page');
const navButtons = document.querySelectorAll('.nav-button');
const loadingSpinner = document.getElementById('loading-spinner');
const errorMessageDiv = document.getElementById('error-message');

// Navigation Buttons
const navCatalogBtn = document.getElementById('nav-catalog');
const navLoginBtn = document.getElementById('nav-auth');
const navAdminDashboardBtn = document.getElementById('nav-admin-dashboard');
const navProviderDashboardBtn = document.getElementById('nav-provider-dashboard');
const navClientDashboardBtn = document.getElementById('nav-client-dashboard');
const navLogoutBtn = document.getElementById('nav-logout');

// Auth Page Elements
const loginForm = document.getElementById('login-form');
const registerForm = document.getElementById('register-form');
const registerUserTypeSelect = document.getElementById('register-usertype');
const providerFieldsDiv = document.getElementById('provider-fields');
const providerCompanySelect = document.getElementById('provider-company-id');

// Catalog Page Elements
const equipmentListDiv = document.getElementById('equipment-list');
const filterNameInput = document.getElementById('filter-name');
const filterTypeSelect = document.getElementById('filter-type');
const filterStateSelect = document.getElementById('filter-state');
const filterCompanySelect = document.getElementById('filter-company');
const filterBrandSelect = document.getElementById('filter-brand');
const applyFiltersBtn = document.getElementById('apply-filters-btn');
const clearFiltersBtn = document.getElementById('clear-filters-btn');

// Equipment Detail Page Elements
const backToCatalogBtn = document.getElementById('back-to-catalog');
const equipmentDetailContentDiv = document.getElementById('equipment-detail-content');
const reviewsListDiv = document.getElementById('reviews-list');
const reviewFormContainer = document.getElementById('review-form-container');
const reviewLoginPrompt = document.getElementById('review-login-prompt');
const reviewStarValueInput = document.getElementById('review-star-value');
const reviewBodyInput = document.getElementById('review-body');
const submitReviewBtn = document.getElementById('submit-review-btn');

// Admin Dashboard Elements
const adminTabs = document.querySelectorAll('#admin-dashboard-page .tab-button');
const adminCompaniesTab = document.getElementById('admin-companies-tab');
const adminBrandsTab = document.getElementById('admin-brands-tab');
const addCompanyBtn = document.getElementById('add-company-btn');
const companiesListDiv = document.getElementById('companies-list');
const addBrandBtn = document.getElementById('add-brand-btn');
const brandsListDiv = document.getElementById('brands-list');

// Provider Dashboard Elements
const providerTabs = document.querySelectorAll('#provider-dashboard-page .tab-button');
const providerEquipmentTab = document.getElementById('provider-equipment-tab');
const providerCompanyTab = document.getElementById('provider-company-tab');
const addEquipmentBtn = document.getElementById('add-equipment-btn');
const myEquipmentListDiv = document.getElementById('my-equipment-list');
const companyDetailsView = document.getElementById('company-details-view');
const viewCompanyName = document.getElementById('view-company-name');
const viewCompanyEmail = document.getElementById('view-company-email');
const viewCompanyPhone = document.getElementById('view-company-phone');
const viewCompanyAddress = document.getElementById('view-company-address');
const viewCompanyWebsite = document.getElementById('view-company-website');
const companyBrandsListDiv = document.getElementById('company-brands-list');

// Client Dashboard Elements
const clientTabs = document.querySelectorAll('#client-dashboard-page .tab-button');
const clientFavoritesTab = document.getElementById('client-favorites-tab');
const clientReviewsTab = document.getElementById('client-reviews-tab');
const myFavoritesListDiv = document.getElementById('my-favorites-list');
const myReviewsListDiv = document.getElementById('my-reviews-list');

// Modals
const companyModal = document.getElementById('company-modal');
const companyModalTitle = document.getElementById('company-modal-title');
const companyForm = document.getElementById('company-form');
const companyIdInput = document.getElementById('company-id');
const companyNameInput = document.getElementById('company-name');
const companyEmailInput = document.getElementById('company-email');
const companyPhoneInput = document.getElementById('company-phone');
const companyAddressInput = document.getElementById('company-address');
const companyWebsiteInput = document.getElementById('company-website');

const manageCompanyBrandsModal = document.getElementById('manage-company-brands-modal');
const manageBrandsCompanyName = document.getElementById('manage-brands-company-name');
const manageBrandsCompanyId = document.getElementById('manage-brands-company-id');
const manageBrandsListDiv = document.getElementById('manage-brands-list');
const associateBrandForm = document.getElementById('associate-brand-form');
const selectBrandToAssociate = document.getElementById('select-brand-to-associate');

const equipmentModal = document.getElementById('equipment-modal');
const equipmentModalTitle = document.getElementById('equipment-modal-title');
const equipmentForm = document.getElementById('equipment-form');
const equipmentIdInput = document.getElementById('equipment-id');
const equipmentNameInput = document.getElementById('equipment-name');
const equipmentDescriptionInput = document.getElementById('equipment-description');
const equipmentPriceInput = document.getElementById('equipment-price');
const equipmentUrlImageInput = document.getElementById('equipment-url-image');
const equipmentTypeIdSelect = document.getElementById('equipment-type-id');
const equipmentStateIdSelect = document.getElementById('equipment-state-id');
const equipmentBrandIdSelect = document.getElementById('equipment-brand-id');

const brandModal = document.getElementById('brand-modal');
const brandModalTitle = document.getElementById('brand-modal-title');
const brandForm = document.getElementById('brand-form');
const brandIdInput = document.getElementById('brand-id');
const brandNameInput = document.getElementById('brand-name');

// --- Utility Functions ---

// Debounce function for search
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// Format currency
function formatCurrency(amount) {
    return new Intl.NumberFormat('es-MX', {
        style: 'currency',
        currency: 'MXN'
    }).format(amount);
}

// Format date
function formatDate(dateString) {
    return new Intl.DateTimeFormat('es-MX', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    }).format(new Date(dateString));
}

// Validate email
function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

// Validate URL
function isValidURL(string) {
    try {
        new URL(string);
        return true;
    } catch (_) {
        return false;
    }
}

// Generate unique ID
function generateId() {
    return Date.now().toString(36) + Math.random().toString(36).substr(2);
}

// --- UI Helper Functions ---

function showLoader() {
    loadingSpinner.classList.remove('hidden');
    document.body.style.overflow = 'hidden';
}

function hideLoader() {
    loadingSpinner.classList.add('hidden');
    document.body.style.overflow = '';
}

function showError(message) {
    hideLoader();
    errorMessageDiv.innerHTML = `
        <i class="fas fa-exclamation-triangle"></i>
        <span>${message}</span>
    `;
    errorMessageDiv.classList.remove('hidden');
    
    // Auto-hide after 5 seconds
    setTimeout(() => {
        errorMessageDiv.classList.add('hidden');
    }, 5000);
    
    // Scroll to top to show error
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

function showSuccess(message) {
    // Create success notification
    const successDiv = document.createElement('div');
    successDiv.className = 'success-notification';
    successDiv.innerHTML = `
        <i class="fas fa-check-circle"></i>
        <span>${message}</span>
    `;
    successDiv.style.cssText = `
        position: fixed;
        top: 20px;
        left: 20px;
        background: linear-gradient(135deg, var(--success-color) 0%, #218838 100%);
        color: white;
        padding: 1rem 1.5rem;
        border-radius: 8px;
        box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        z-index: 1000;
        display: flex;
        align-items: center;
        gap: 0.5rem;
        animation: slideInLeft 0.3s ease;
        max-width: 300px;
    `;
    
    document.body.appendChild(successDiv);
    
    setTimeout(() => {
        successDiv.style.animation = 'slideOutLeft 0.3s ease';
        setTimeout(() => {
            if (successDiv.parentNode) {
                successDiv.parentNode.removeChild(successDiv);
            }
        }, 300);
    }, 3000);
}

function showPage(pageId) {
    // Hide all pages
    pages.forEach(page => {
        page.classList.add('hidden');
        page.style.display = 'none';
    });
    
    // Show target page
    const targetPage = document.getElementById(pageId);
    if (targetPage) {
        targetPage.classList.remove('hidden');
        targetPage.style.display = 'block';
    }
    
    // Update navigation
    navButtons.forEach(btn => btn.classList.remove('active'));
    const navBtnId = `nav-${pageId.replace('-page', '')}`;
    const navBtnToActivate = document.getElementById(navBtnId);
    if (navBtnToActivate) {
        navBtnToActivate.classList.add('active');
    }
    
    // Scroll to top
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

function updateNavButtons() {
    const isLoggedIn = CURRENT_USER !== null;
    const isClient = USER_ROLE && USER_ROLE.toLowerCase() === 'cliente';
    const isProvider = USER_ROLE && USER_ROLE.toLowerCase() === 'proveedor';
    const isAdmin = USER_ROLE && USER_ROLE.toLowerCase() === 'administrador';

    navLoginBtn.classList.toggle('hidden', isLoggedIn);
    navLogoutBtn.classList.toggle('hidden', !isLoggedIn);
    navClientDashboardBtn.classList.toggle('hidden', !isLoggedIn || !isClient);
    navProviderDashboardBtn.classList.toggle('hidden', !isLoggedIn || !isProvider);
    navAdminDashboardBtn.classList.toggle('hidden', !isLoggedIn || !isAdmin);
}

function isAuthenticated() {
    return USER_TOKEN !== null;
}

function isClient() {
    return isAuthenticated() && USER_ROLE && USER_ROLE.toLowerCase() === 'cliente';
}

function isProvider() {
    return isAuthenticated() && USER_ROLE && USER_ROLE.toLowerCase() === 'proveedor';
}

function isAdmin() {
    return isAuthenticated() && USER_ROLE && USER_ROLE.toLowerCase() === 'administrador';
}

// --- API Functions ---

async function fetchApi(endpoint, method = 'GET', body = null, requiresAuth = false) {
    showLoader();
    errorMessageDiv.classList.add('hidden');

    const headers = {
        'Content-Type': 'application/json',
    };

    if (requiresAuth && USER_TOKEN) {
        headers['Authorization'] = `Bearer ${USER_TOKEN}`;
    }

    try {
        const options = { method, headers };
        if (body) {
            options.body = JSON.stringify(body);
        }

        const response = await fetch(`${API_BASE_URL}${endpoint}`, options);
        let data;
        const contentType = response.headers.get('content-type');
        if (contentType && contentType.includes('application/json')) {
            data = await response.json();
        } else {
            const textData = await response.text();
            data = { message: textData || `Error ${response.status}: ${response.statusText}` };
        }

        if (!response.ok) {
            const errorMsg = data.message || `Error ${response.status}: ${response.statusText}`;
            throw new Error(errorMsg);
        }
        return data;
    } catch (error) {
        console.error('API Error:', error);
        showError(error.message);
        throw error;
    } finally {
        hideLoader();
    }
}

// --- Authentication Functions ---

async function handleLogin(event) {
    event.preventDefault();
    const email = document.getElementById('login-email').value.trim();
    const password = document.getElementById('login-password').value;

    // Validation
    if (!isValidEmail(email)) {
        showError('Por favor ingresa un email válido');
        return;
    }

    if (password.length < 6) {
        showError('La contraseña debe tener al menos 6 caracteres');
        return;
    }

    try {
        const data = await fetchApi('/login', 'POST', { email, password });
        localStorage.setItem('userToken', data.token);
        localStorage.setItem('currentUserId', data.userId);
        localStorage.setItem('userRole', data.userRole);
        if (data.companyId) {
            localStorage.setItem('userCompanyId', data.companyId);
        } else {
            localStorage.removeItem('userCompanyId');
        }

        CURRENT_USER = data.userId;
        USER_TOKEN = data.token;
        USER_ROLE = data.userRole;
        USER_COMPANY_ID = data.companyId || null;

        showSuccess('Inicio de sesión exitoso');
        loginForm.reset();
        updateAuthUI();
        
        // Redirigir según el rol del usuario
        if (USER_ROLE === 'administrador') {
            showPage('admin-dashboard-page');
            await loadAdminDashboard();
        } else if (USER_ROLE === 'proveedor') {
            showPage('provider-dashboard-page');
            await loadProviderDashboard();
        } else if (USER_ROLE === 'cliente') {
            showPage('catalog-page');
            await loadCatalog();
        } else {
            // Fallback por si hay un rol no reconocido
            showPage('catalog-page');
            await loadCatalog();
        }
    } catch (error) {
        // Error already displayed by fetchApi
    }
}

async function handleRegister(event) {
    event.preventDefault();
    const fullName = document.getElementById('register-fullname').value.trim();
    const email = document.getElementById('register-email').value.trim();
    const password = document.getElementById('register-password').value;
    const userType = registerUserTypeSelect.value;

    // Validation
    if (!fullName || fullName.length < 2) {
        showError('El nombre debe tener al menos 2 caracteres');
        return;
    }

    if (!isValidEmail(email)) {
        showError('Por favor ingresa un email válido');
        return;
    }

    if (password.length < 6) {
        showError('La contraseña debe tener al menos 6 caracteres');
        return;
    }

    if (!userType) {
        showError('Por favor selecciona un tipo de usuario');
        return;
    }

    const registerData = { fullName, email, password, userType };

    if (userType === 'proveedor') {
        const companyId = providerCompanySelect.value;
        if (!companyId) {
            showError('Por favor selecciona una empresa');
            return;
        }
        registerData.companyId = parseInt(companyId);
    }

    try {
        const data = await fetchApi('/register', 'POST', registerData);
        localStorage.setItem('userToken', data.token);
        localStorage.setItem('currentUserId', data.userId);
        localStorage.setItem('userRole', data.userRole);
        if (data.companyId) {
            localStorage.setItem('userCompanyId', data.companyId);
        }

        CURRENT_USER = data.userId;
        USER_TOKEN = data.token;
        USER_ROLE = data.userRole;
        USER_COMPANY_ID = data.companyId || null;

        showSuccess('Registro exitoso');
        registerForm.reset();
        providerFieldsDiv.classList.add('hidden');
        updateAuthUI();
        showPage('catalog-page');
        loadCatalog();
    } catch (error) {
        // Error already displayed by fetchApi
    }
}

function handleLogout() {
    if (!confirm('¿Estás seguro de que quieres cerrar sesión?')) return;
    
    localStorage.removeItem('userToken');
    localStorage.removeItem('currentUserId');
    localStorage.removeItem('userRole');
    localStorage.removeItem('userCompanyId');

    CURRENT_USER = null;
    USER_TOKEN = null;
    USER_ROLE = null;
    USER_COMPANY_ID = null;

    showSuccess('Sesión cerrada correctamente');
    updateAuthUI();
    showPage('catalog-page');
    loadCatalog();
}

function updateAuthUI() {
    updateNavButtons();
    
    // Reset both forms
    loginForm?.reset();
    registerForm?.reset();
    
    // Reset register form state
    registerUserTypeSelect.value = '';
    providerFieldsDiv.classList.add('hidden');
    
    // Remove required attribute from company select when hidden
    const providerCompanySelect = document.getElementById('provider-company-id');
    if (providerCompanySelect) {
        providerCompanySelect.removeAttribute('required');
        providerCompanySelect.value = '';
    }
    
    // Always show login form first when auth page is accessed
    document.getElementById('login-section')?.classList.remove('hidden');
    document.getElementById('register-section')?.classList.add('hidden');

    if (isClient()) {
        reviewFormContainer?.classList.remove('hidden');
        reviewLoginPrompt?.classList.add('hidden');
    } else {
        reviewFormContainer?.classList.add('hidden');
        reviewLoginPrompt?.classList.remove('hidden');
    }
}

// --- Catalog Functions ---

async function loadCatalog() {
    try {
        const equipment = await fetchApi('/equipment');
        renderEquipmentList(equipment, equipmentListDiv, 'catalog');
        await loadFilterOptions();
    } catch (error) {
        equipmentListDiv.innerHTML = `
            <div class="empty-state">
                <i class="fas fa-exclamation-triangle"></i>
                <h3>Error al cargar el catálogo</h3>
                <p>No se pudieron cargar los equipos. Intenta recargar la página.</p>
            </div>
        `;
    }
}

async function loadFilterOptions() {
    try {
        const [types, states, companies, brands] = await Promise.all([
            fetchApi('/types-equipment'),
            fetchApi('/states-equipment'),
            fetchApi('/companies'),
            fetchApi('/brands')
        ]);
        
        populateSelect(filterTypeSelect, types, 'id', 'type', 'Todos los tipos');
        populateSelect(filterStateSelect, states, 'id', 'state', 'Todos los estados');
        populateSelect(filterCompanySelect, companies, 'id', 'name', 'Todas las empresas');
        populateSelect(filterBrandSelect, brands, 'id', 'nameBrand', 'Todas las marcas');
    } catch (error) {
        console.error('Error cargando opciones de filtro:', error);
    }
}

// Debounced search function
const debouncedApplyFilters = debounce(async () => {
    const name = filterNameInput.value.trim();
    const typeId = filterTypeSelect.value;
    const stateId = filterStateSelect.value;
    const companyId = filterCompanySelect.value;
    const brandId = filterBrandSelect.value;

    const params = new URLSearchParams();
    if (name) params.append('name', name);
    if (typeId) params.append('typeId', typeId);
    if (stateId) params.append('stateId', stateId);
    if (companyId) params.append('companyId', companyId);
    if (brandId) params.append('brandId', brandId);

    try {
        const equipment = await fetchApi(`/equipment/search?${params.toString()}`);
        renderEquipmentList(equipment, equipmentListDiv, 'catalog');
    } catch (error) {
        showError('Error al buscar equipos');
    }
}, 300);

async function applyFilters() {
    debouncedApplyFilters();
}

function clearFilters() {
    filterNameInput.value = '';
    filterTypeSelect.value = '';
    filterStateSelect.value = '';
    filterCompanySelect.value = '';
    filterBrandSelect.value = '';
    loadCatalog();
}

function renderEquipmentList(equipmentList, container, context = 'catalog') {
    container.innerHTML = '';
    
    if (equipmentList.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <i class="fas fa-search"></i>
                <h3>No se encontraron equipos</h3>
                <p>Intenta ajustar los filtros de búsqueda o explora todas las categorías.</p>
            </div>
        `;
        return;
    }

    equipmentList.forEach((equipment, index) => {
        const card = document.createElement('div');
        card.className = 'equipment-card';
        card.style.animationDelay = `${index * 0.1}s`;
        
        const price = typeof equipment.price === 'number' ? formatCurrency(equipment.price) : equipment.price;
        
        card.innerHTML = `
            <img src="${equipment.urlImage || 'https://via.placeholder.com/300x200?text=Sin+Imagen'}" 
                 alt="${equipment.name}" 
                 class="equipment-card-img"
                 onerror="this.src='https://via.placeholder.com/300x200?text=Sin+Imagen'">
            <div class="equipment-card-content">
                <div class="equipment-card-title">${equipment.name}</div>
                <div class="equipment-card-details">
                    <span class="detail-item">
                        <i class="fas fa-tag"></i> 
                        ${equipment.brandName || 'Sin marca'}
                    </span>
                    <span class="detail-item">
                        <i class="fas fa-cogs"></i> 
                        ${equipment.typeName || 'Sin tipo'}
                    </span>
                    <span class="detail-item">
                        <i class="fas fa-star"></i> 
                        ${equipment.stateName || 'Sin estado'}
                    </span>
                    ${equipment.companyName ? `
                        <span class="detail-item">
                            <i class="fas fa-building"></i> 
                            ${equipment.companyName}
                        </span>
                    ` : ''}
                </div>
                <div class="equipment-card-price">${price}</div>
                
                ${context === 'catalog' || context === 'provider-dashboard' ? 
                    `<button class="btn btn-primary view-detail-btn" data-id="${equipment.id}">
                        <i class="fas fa-eye"></i> Ver Detalles
                    </button>` : ''}
                
                <div class="card-buttons">
                    ${context === 'provider-dashboard' ? `
                        <button class="btn btn-secondary edit-btn" data-id="${equipment.id}">
                            <i class="fas fa-edit"></i> Editar
                        </button>
                        <button class="btn btn-danger delete-btn" data-id="${equipment.id}">
                            <i class="fas fa-trash"></i> Eliminar
                        </button>
                    ` : ''}
                    ${context === 'client-favorites' ? `
                        <button class="btn btn-danger delete-btn" data-favorite-id="${equipment.favoriteId}">
                            <i class="fas fa-heart-broken"></i> Quitar de Favoritos
                        </button>
                    ` : ''}
                </div>
            </div>
        `;

        // Event listeners
        if (context === 'catalog' || context === 'provider-dashboard') {
            const viewBtn = card.querySelector('.view-detail-btn');
            if (viewBtn) {
                viewBtn.addEventListener('click', (e) => {
                    const equipmentId = e.currentTarget.dataset.id;
                    loadEquipmentDetail(equipmentId);
                });
            }
        }
        
        if (context === 'provider-dashboard') {
            const editBtn = card.querySelector('.edit-btn');
            const deleteBtn = card.querySelector('.delete-btn');
            
            if (editBtn) {
                editBtn.addEventListener('click', (e) => {
                    openEquipmentModalForEdit(e.currentTarget.dataset.id);
                });
            }
            
            if (deleteBtn) {
                deleteBtn.addEventListener('click', (e) => {
                    deleteEquipment(e.currentTarget.dataset.id);
                });
            }
        }
        
        if (context === 'client-favorites') {
            const deleteBtn = card.querySelector('.delete-btn');
            if (deleteBtn) {
                deleteBtn.addEventListener('click', (e) => {
                    removeFavorite(e.currentTarget.dataset.favoriteId);
                });
            }
        }

        container.appendChild(card);
    });
}

// --- Equipment Detail Functions ---

async function loadEquipmentDetail(equipmentId) {
    try {
        const equipment = await fetchApi(`/equipment/${equipmentId}`);
        renderEquipmentDetail(equipment);
        loadReviewsForEquipment(equipmentId);
        showPage('equipment-detail-page');
    } catch (error) {
        showError('Error al cargar los detalles del equipo');
        showPage('catalog-page');
    }
}

function renderEquipmentDetail(equipment) {
    const price = typeof equipment.price === 'number' ? formatCurrency(equipment.price) : equipment.price;
    const createdDate = formatDate(equipment.createdAt);
    
    equipmentDetailContentDiv.innerHTML = `
        <img id="equipment-detail-image" 
             src="${equipment.urlImage || 'https://via.placeholder.com/400x300?text=Sin+Imagen'}" 
             alt="${equipment.name}"
             onerror="this.src='https://via.placeholder.com/400x300?text=Sin+Imagen'">
        <div class="detail-info">
            <h2>${equipment.name}</h2>
            <p><strong>Descripción:</strong> ${equipment.description || 'Sin descripción disponible.'}</p>
            <p><strong>Precio:</strong> ${price}</p>
            <p><strong>Marca:</strong> ${equipment.brandName || 'Sin marca'}</p>
            <p><strong>Tipo:</strong> ${equipment.typeName || 'Sin tipo'}</p>
            <p><strong>Estado:</strong> ${equipment.stateName || 'Sin estado'}</p>
            <p><strong>Proveedor:</strong> ${equipment.providerName || 'Sin proveedor'}</p>
            ${equipment.companyName ? `<p><strong>Empresa:</strong> ${equipment.companyName}</p>` : ''}
            <p><strong>Publicado:</strong> ${createdDate}</p>
            ${isClient() ? `
                <button id="add-to-favorites-btn" class="btn btn-primary" data-id="${equipment.id}">
                    <i class="fas fa-heart"></i> Añadir a Favoritos
                </button>
            ` : ''}
        </div>
    `;

    // Add to favorites functionality
    if (isClient()) {
        const favoriteBtn = document.getElementById('add-to-favorites-btn');
        if (favoriteBtn) {
            favoriteBtn.addEventListener('click', (e) => {
                addToFavorites(e.currentTarget.dataset.id);
            });
        }
    }

    // Reset review form
    reviewStarValueInput.value = '';
    reviewBodyInput.value = '';
    submitReviewBtn.dataset.equipmentId = equipment.id;
    
    // Reset star rating display
    const stars = document.querySelectorAll('.star-rating-input .star');
    stars.forEach(star => star.classList.remove('selected'));
    
    updateAuthUI();
}

// --- Review Functions ---

async function loadReviewsForEquipment(equipmentId) {
    try {
        const reviews = await fetchApi(`/equipment/${equipmentId}/reviews`);
        renderReviewsList(reviews, reviewsListDiv, 'equipment');
    } catch (error) {
        reviewsListDiv.innerHTML = `
            <div class="empty-state">
                <i class="fas fa-comment-slash"></i>
                <h3>Error al cargar reseñas</h3>
                <p>No se pudieron cargar las reseñas de este equipo.</p>
            </div>
        `;
    }
}

async function submitReview(event) {
    event.preventDefault();
    const equipmentId = parseInt(submitReviewBtn.dataset.equipmentId);
    const starValue = parseInt(reviewStarValueInput.value);
    const body = reviewBodyInput.value.trim();

    // Validation
    if (!starValue || starValue < 1 || starValue > 5) {
        showError('Por favor, selecciona una calificación entre 1 y 5 estrellas.');
        return;
    }

    if (!body || body.length < 10) {
        showError('La reseña debe tener al menos 10 caracteres.');
        return;
    }

    try {
        await fetchApi('/reviews', 'POST', { equipmentId, starValue, body }, true);
        showSuccess('Reseña enviada con éxito');
        
        // Reset form
        reviewStarValueInput.value = '';
        reviewBodyInput.value = '';
        const stars = document.querySelectorAll('.star-rating-input .star');
        stars.forEach(star => star.classList.remove('selected'));
        
        // Reload reviews
        loadReviewsForEquipment(equipmentId);
        if (isClient()) loadMyReviews();
    } catch (error) {
        // Error already displayed
    }
}

function renderReviewsList(reviews, container, context = 'equipment') {
    container.innerHTML = '';
    
    if (reviews.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <i class="fas fa-comment"></i>
                <h3>Sin reseñas</h3>
                <p>Este equipo aún no tiene reseñas. ¡Sé el primero en dejar una!</p>
            </div>
        `;
        return;
    }

    reviews.forEach((review, index) => {
        const reviewItem = document.createElement('div');
        reviewItem.className = 'review-item';
        reviewItem.style.animationDelay = `${index * 0.1}s`;
        
        const reviewDate = formatDate(review.createdAt);
        const stars = '★'.repeat(review.starValue) + '☆'.repeat(5 - review.starValue);
        
        reviewItem.innerHTML = `
            <div>
                <p class="review-meta">
                    <strong>${review.userName}</strong> - 
                    <span style="color: #FFD700;">${stars}</span> 
                    (${reviewDate})
                </p>
                <p class="review-body">${review.body || 'Sin comentario.'}</p>
                ${context === 'client-dashboard' ? `
                    <p><strong>Equipo:</strong> ${review.equipmentName}</p>
                ` : ''}
            </div>
            ${context === 'client-dashboard' ? `
                <button class="btn btn-danger delete-btn" data-id="${review.id}">
                    <i class="fas fa-trash"></i> Eliminar
                </button>
            ` : ''}
        `;
        
        if (context === 'client-dashboard') {
            const deleteBtn = reviewItem.querySelector('.delete-btn');
            if (deleteBtn) {
                deleteBtn.addEventListener('click', (e) => {
                    deleteReview(e.currentTarget.dataset.id);
                });
            }
        }
        
        container.appendChild(reviewItem);
    });
}

async function loadMyReviews() {
    if (!isClient()) return;
    try {
        const reviews = await fetchApi(`/users/${CURRENT_USER}/reviews`, 'GET', null, true);
        renderReviewsList(reviews, myReviewsListDiv, 'client-dashboard');
    } catch (error) {
        myReviewsListDiv.innerHTML = `
            <div class="empty-state">
                <i class="fas fa-comment-slash"></i>
                <h3>Error al cargar reseñas</h3>
                <p>No se pudieron cargar tus reseñas.</p>
            </div>
        `;
    }
}

async function deleteReview(reviewId) {
    if (!confirm('¿Estás seguro de que quieres eliminar esta reseña?')) return;
    
    try {
        await fetchApi(`/reviews/${reviewId}`, 'DELETE', null, true);
        showSuccess('Reseña eliminada con éxito');
        
        const currentEquipmentId = submitReviewBtn.dataset.equipmentId;
        if (currentEquipmentId) {
            loadReviewsForEquipment(currentEquipmentId);
        }
        loadMyReviews();
    } catch (error) {
        // Error already displayed
    }
}

// --- Favorite Functions ---

async function addToFavorites(equipmentId) {
    if (!isClient()) {
        showError('Debes iniciar sesión como cliente para añadir a favoritos.');
        return;
    }
    
    try {
        await fetchApi('/favorites', 'POST', { equipmentId: parseInt(equipmentId) }, true);
        showSuccess('Equipo añadido a favoritos');
        if (isClient()) loadMyFavorites();
    } catch (error) {
        // Error already displayed
    }
}

async function removeFavorite(favoriteId) {
    if (!isClient()) return;
    if (!confirm('¿Estás seguro de que quieres eliminar este favorito?')) return;
    
    try {
        await fetchApi(`/favorites/${favoriteId}`, 'DELETE', null, true);
        showSuccess('Favorito eliminado con éxito');
        loadMyFavorites();
    } catch (error) {
        // Error already displayed
    }
}

async function loadMyFavorites() {
    if (!isClient()) return;
    
    try {
        const favorites = await fetchApi(`/users/${CURRENT_USER}/favorites`, 'GET', null, true);
        const equipmentPromises = favorites.map(f =>
            fetchApi(`/equipment/${f.idEquipment}`).then(eq => ({ ...eq, favoriteId: f.id }))
        );
        const equipmentDetails = await Promise.all(equipmentPromises);
        renderEquipmentList(equipmentDetails, myFavoritesListDiv, 'client-favorites');
    } catch (error) {
        myFavoritesListDiv.innerHTML = `
            <div class="empty-state">
                <i class="fas fa-heart-broken"></i>
                <h3>Error al cargar favoritos</h3>
                <p>No se pudieron cargar tus equipos favoritos.</p>
            </div>
        `;
    }
}

// --- Admin Dashboard Functions ---

async function loadAdminDashboard() {
    if (!isAdmin()) {
        showError('Acceso denegado. Solo administradores.');
        showPage('catalog-page');
        await loadCatalog();
        return;
    }
    
    // Ensure we're on the right page
    showPage('admin-dashboard-page');
    
    try {
        // Load both sections in parallel for better performance
        await Promise.all([
            loadCompanies(),
            loadBrands()
        ]);
    } catch (error) {
        console.error('Error loading admin dashboard:', error);
        showError('Error al cargar el panel de administrador');
    }
}

async function loadCompanies() {
    try {
        const companies = await fetchApi('/companies');
        renderCompaniesList(companies);
    } catch (error) {
        companiesListDiv.innerHTML = `
            <div class="empty-state">
                <i class="fas fa-building"></i>
                <h3>Error al cargar empresas</h3>
                <p>No se pudieron cargar las empresas.</p>
            </div>
        `;
    }
}

function renderCompaniesList(companies) {
    companiesListDiv.innerHTML = '';
    
    if (companies.length === 0) {
        companiesListDiv.innerHTML = `
            <div class="empty-state">
                <i class="fas fa-building"></i>
                <h3>Sin empresas registradas</h3>
                <p>No hay empresas en el sistema. Añade la primera empresa.</p>
            </div>
        `;
        return;
    }

    companies.forEach((company, index) => {
        const item = document.createElement('div');
        item.className = 'list-item';
        item.style.animationDelay = `${index * 0.1}s`;
        
        item.innerHTML = `
            <div class="list-item-content">
                <h4>${company.name}</h4>
                <p>
                    ${company.email ? `<i class="fas fa-envelope"></i> ${company.email}` : 'Sin email'} 
                    ${company.email && company.phone ? ' | ' : ''}
                    ${company.phone ? `<i class="fas fa-phone"></i> ${company.phone}` : 'Sin teléfono'}
                </p>
                ${company.address ? `<p><i class="fas fa-map-marker-alt"></i> ${company.address}</p>` : ''}
                ${company.webSite ? `<p><i class="fas fa-globe"></i> <a href="${company.webSite}" target="_blank">${company.webSite}</a></p>` : ''}
            </div>
            <div class="list-item-buttons">
                <button class="btn btn-primary manage-brands-btn" data-id="${company.id}">
                    <i class="fas fa-tags"></i> Gestionar Marcas
                </button>
                <button class="btn btn-secondary edit-btn" data-id="${company.id}">
                    <i class="fas fa-edit"></i> Editar
                </button>
                <button class="btn btn-danger delete-btn" data-id="${company.id}">
                    <i class="fas fa-trash"></i> Eliminar
                </button>
            </div>
        `;
        
        // Event listeners
        item.querySelector('.manage-brands-btn').addEventListener('click', (e) => {
            openManageCompanyBrandsModal(e.currentTarget.dataset.id);
        });
        item.querySelector('.edit-btn').addEventListener('click', (e) => {
            openCompanyModalForEdit(e.currentTarget.dataset.id);
        });
        item.querySelector('.delete-btn').addEventListener('click', (e) => {
            deleteCompany(e.currentTarget.dataset.id);
        });
        
        companiesListDiv.appendChild(item);
    });
}

async function loadBrands() {
    try {
        const brands = await fetchApi('/brands');
        renderBrandsList(brands);
    } catch (error) {
        brandsListDiv.innerHTML = `
            <div class="empty-state">
                <i class="fas fa-tags"></i>
                <h3>Error al cargar marcas</h3>
                <p>No se pudieron cargar las marcas.</p>
            </div>
        `;
    }
}

function renderBrandsList(brands) {
    brandsListDiv.innerHTML = '';
    
    if (brands.length === 0) {
        brandsListDiv.innerHTML = `
            <div class="empty-state">
                <i class="fas fa-tags"></i>
                <h3>Sin marcas registradas</h3>
                <p>No hay marcas en el sistema. Añade la primera marca.</p>
            </div>
        `;
        return;
    }

    brands.forEach((brand, index) => {
        const item = document.createElement('div');
        item.className = 'list-item';
        item.style.animationDelay = `${index * 0.1}s`;
        
        item.innerHTML = `
            <div class="list-item-content">
                <h4>${brand.nameBrand}</h4>
            </div>
            <div class="list-item-buttons">
                <button class="btn btn-secondary edit-btn" data-id="${brand.id}">
                    <i class="fas fa-edit"></i> Editar
                </button>
                <button class="btn btn-danger delete-btn" data-id="${brand.id}">
                    <i class="fas fa-trash"></i> Eliminar
                </button>
            </div>
        `;
        
        // Event listeners
        item.querySelector('.edit-btn').addEventListener('click', (e) => {
            openBrandModalForEdit(e.currentTarget.dataset.id);
        });
        item.querySelector('.delete-btn').addEventListener('click', (e) => {
            deleteBrand(e.currentTarget.dataset.id);
        });
        
        brandsListDiv.appendChild(item);
    });
}

// Company Management Functions
async function openCompanyModalForEdit(companyId = null) {
    companyModal.classList.remove('hidden');
    
    if (companyId) {
        companyModalTitle.innerHTML = '<i class="fas fa-edit"></i> Editar Empresa';
        try {
            const company = await fetchApi(`/companies/${companyId}`);
            companyIdInput.value = company.id;
            companyNameInput.value = company.name;
            companyEmailInput.value = company.email || '';
            companyPhoneInput.value = company.phone || '';
            companyAddressInput.value = company.address || '';
            companyWebsiteInput.value = company.webSite || '';
        } catch (error) {
            showError('Error al cargar datos de la empresa');
            companyModal.classList.add('hidden');
        }
    } else {
        companyModalTitle.innerHTML = '<i class="fas fa-plus"></i> Añadir Empresa';
        companyForm.reset();
        companyIdInput.value = '';
    }
}

async function handleCompanyFormSubmit(event) {
    event.preventDefault();
    const companyId = companyIdInput.value;
    const method = companyId ? 'PUT' : 'POST';
    const url = companyId ? `/companies/${companyId}` : '/companies';

    // Validation
    const name = companyNameInput.value.trim();
    const email = companyEmailInput.value.trim();
    const website = companyWebsiteInput.value.trim();

    if (!name || name.length < 2) {
        showError('El nombre de la empresa debe tener al menos 2 caracteres');
        return;
    }

    if (email && !isValidEmail(email)) {
        showError('Por favor ingresa un email válido');
        return;
    }

    if (website && !isValidURL(website)) {
        showError('Por favor ingresa una URL válida para el sitio web');
        return;
    }

    const companyData = {
        name,
        email: email || null,
        phone: companyPhoneInput.value.trim() || null,
        address: companyAddressInput.value.trim() || null,
        webSite: website || null
    };

    if (companyId) {
        companyData.id = parseInt(companyId);
    }

    try {
        await fetchApi(url, method, companyData, true);
        showSuccess(companyId ? 'Empresa actualizada con éxito' : 'Empresa añadida con éxito');
        companyModal.classList.add('hidden');
        loadCompanies();
        loadCompaniesForRegister();
    } catch (error) {
        // Error already displayed
    }
}

async function deleteCompany(companyId) {
    if (!confirm('¿Estás seguro de que quieres eliminar esta empresa?\n\nEsta acción es irreversible y puede afectar a proveedores y equipos asociados.')) return;
    
    try {
        await fetchApi(`/companies/${companyId}`, 'DELETE', null, true);
        showSuccess('Empresa eliminada con éxito');
        loadCompanies();
        loadCompaniesForRegister();
    } catch (error) {
        // Error already displayed
    }
}

// --- Utility Functions ---

function populateSelect(selectElement, data, valueKey, textKey, defaultText = 'Selecciona una opción') {
    selectElement.innerHTML = `<option value="">${defaultText}</option>`;
    data.forEach(item => {
        const option = document.createElement('option');
        option.value = item[valueKey];
        option.textContent = item[textKey];
        selectElement.appendChild(option);
    });
}

async function loadCompaniesForRegister() {
    try {
        const companies = await fetchApi('/companies');
        populateSelect(providerCompanySelect, companies, 'id', 'name', 'Selecciona una empresa');
    } catch (error) {
        console.error('Error cargando empresas para registro:', error);
    }
}

// --- Modal Management ---

// Global close button for modals
document.querySelectorAll('.modal .close-button').forEach(button => {
    button.addEventListener('click', (e) => {
        e.target.closest('.modal').classList.add('hidden');
    });
});

// Close modal when clicking outside content
document.querySelectorAll('.modal').forEach(modal => {
    modal.addEventListener('click', (e) => {
        if (e.target === modal) {
            modal.classList.add('hidden');
        }
    });
});

// Escape key to close modals
document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') {
        document.querySelectorAll('.modal:not(.hidden)').forEach(modal => {
            modal.classList.add('hidden');
        });
    }
});

// --- Event Listeners ---

// Navigation
navCatalogBtn?.addEventListener('click', () => { 
    showPage('catalog-page'); 
    loadCatalog(); 
});
navLoginBtn?.addEventListener('click', () => showPage('auth-page'));
navLogoutBtn?.addEventListener('click', handleLogout);
navAdminDashboardBtn?.addEventListener('click', async () => {
    await loadAdminDashboard();
});
navProviderDashboardBtn?.addEventListener('click', async () => {
    await loadProviderDashboard();
});
navClientDashboardBtn?.addEventListener('click', async () => { 
    showPage('client-dashboard-page'); 
    await loadMyFavorites(); 
});
backToCatalogBtn?.addEventListener('click', () => { 
    showPage('catalog-page'); 
    loadCatalog(); 
});

// Auth Forms
loginForm?.addEventListener('submit', handleLogin);
registerForm?.addEventListener('submit', handleRegister);

// Auth form switching
document.getElementById('show-register')?.addEventListener('click', (e) => {
    e.preventDefault();
    document.getElementById('login-section').classList.add('hidden');
    document.getElementById('register-section').classList.remove('hidden');
});

document.getElementById('show-login')?.addEventListener('click', (e) => {
    e.preventDefault();
    document.getElementById('register-section').classList.add('hidden');
    document.getElementById('login-section').classList.remove('hidden');
});

// Password toggle functionality
document.addEventListener('click', (e) => {
    if (e.target.closest('.password-toggle')) {
        const toggle = e.target.closest('.password-toggle');
        const targetId = toggle.dataset.target;
        const passwordInput = document.getElementById(targetId);
        const icon = toggle.querySelector('i');
        
        if (passwordInput.type === 'password') {
            passwordInput.type = 'text';
            icon.classList.remove('fa-eye');
            icon.classList.add('fa-eye-slash');
        } else {
            passwordInput.type = 'password';
            icon.classList.remove('fa-eye-slash');
            icon.classList.add('fa-eye');
        }
    }
});

registerUserTypeSelect?.addEventListener('change', async (e) => {
    const providerCompanySelect = document.getElementById('provider-company-id');
    
    if (e.target.value === 'proveedor') {
        providerFieldsDiv.classList.remove('hidden');
        // Add required attribute only when visible
        providerCompanySelect.setAttribute('required', 'required');
        await loadCompaniesForRegister();
    } else {
        providerFieldsDiv.classList.add('hidden');
        // Remove required attribute when hidden
        providerCompanySelect.removeAttribute('required');
        providerCompanySelect.value = '';
    }
});

// Catalog Filters
applyFiltersBtn?.addEventListener('click', applyFilters);
clearFiltersBtn?.addEventListener('click', clearFilters);

// Real-time search
filterNameInput?.addEventListener('input', debouncedApplyFilters);
filterTypeSelect?.addEventListener('change', applyFilters);
filterStateSelect?.addEventListener('change', applyFilters);
filterCompanySelect?.addEventListener('change', applyFilters);
filterBrandSelect?.addEventListener('change', applyFilters);

// Review Submit
submitReviewBtn?.addEventListener('click', submitReview);

// Admin Dashboard Tabs
adminTabs.forEach(tab => {
    tab.addEventListener('click', (e) => {
        document.querySelectorAll('#admin-dashboard-page .tab-content').forEach(content => 
            content.classList.add('hidden')
        );
        document.querySelectorAll('#admin-dashboard-page .tab-button').forEach(btn => 
            btn.classList.remove('active')
        );
        document.getElementById(`${e.target.dataset.tab}-tab`).classList.remove('hidden');
        e.target.classList.add('active');

        if (e.target.dataset.tab === 'admin-companies') loadCompanies();
        else if (e.target.dataset.tab === 'admin-brands') loadBrands();
    });
});

// Admin Dashboard Actions
addCompanyBtn?.addEventListener('click', () => openCompanyModalForEdit(null));
companyForm?.addEventListener('submit', handleCompanyFormSubmit);
addBrandBtn?.addEventListener('click', () => openBrandModalForEdit(null));

// --- Initialization ---
async function init() {
    // Load data from localStorage
    const token = localStorage.getItem('userToken');
    const userId = localStorage.getItem('currentUserId');
    const role = localStorage.getItem('userRole');
    const companyId = localStorage.getItem('userCompanyId');
    
    // Only assign if valid values exist
    if (token && userId && role) {
        USER_TOKEN = token;
        CURRENT_USER = userId;
        USER_ROLE = role;
        USER_COMPANY_ID = companyId || null;
    } else {
        // Clear if no valid session
        USER_TOKEN = null;
        CURRENT_USER = null;
        USER_ROLE = null;
        USER_COMPANY_ID = null;
    }

    updateAuthUI();
    
    // Show appropriate page based on user role and load content
    try {
        if (isAdmin()) {
            showPage('admin-dashboard-page');
            await loadAdminDashboard();
        } else if (isProvider()) {
            showPage('provider-dashboard-page');
            await loadProviderDashboard();
        } else if (isClient()) {
            showPage('catalog-page');
            await loadCatalog();
        } else {
            // No authenticated user, show catalog
            showPage('catalog-page');
            await loadCatalog();
        }
    } catch (error) {
        console.error('Error during initialization:', error);
        showPage('catalog-page');
        await loadCatalog();
    }
    
    // Add CSS animations
    addDynamicStyles();
}

// Add dynamic CSS animations
function addDynamicStyles() {
    const style = document.createElement('style');
    style.textContent = `
        @keyframes slideInLeft {
            from {
                opacity: 0;
                transform: translateX(-100px);
            }
            to {
                opacity: 1;
                transform: translateX(0);
            }
        }
        
        @keyframes slideOutLeft {
            from {
                opacity: 1;
                transform: translateX(0);
            }
            to {
                opacity: 0;
                transform: translateX(-100px);
            }
        }
        
        @keyframes slideInRight {
            from {
                opacity: 0;
                transform: translateX(100px);
            }
            to {
                opacity: 1;
                transform: translateX(0);
            }
        }
        
        @keyframes slideOutRight {
            from {
                opacity: 1;
                transform: translateX(0);
            }
            to {
                opacity: 0;
                transform: translateX(100px);
            }
        }
        
        .success-notification {
            animation: slideInLeft 0.3s ease !important;
        }
    `;
    document.head.appendChild(style);
}

// --- Provider Dashboard Functions (Continued) ---

async function loadProviderDashboard() {
    if (!isProvider()) {
        showError('Acceso denegado. Solo proveedores.');
        showPage('catalog-page');
        await loadCatalog();
        return;
    }
    
    // Ensure we're on the right page
    showPage('provider-dashboard-page');
    
    try {
        // Load both sections in parallel for better performance
        await Promise.all([
            loadMyEquipment(),
            loadCompanyDetails()
        ]);
    } catch (error) {
        console.error('Error loading provider dashboard:', error);
        showError('Error al cargar el panel de proveedor');
    }
}

async function loadMyEquipment() {
    if (!isProvider()) return;
    
    try {
        const providerData = await fetchApi(`/users/${CURRENT_USER}/provider`, 'GET', null, true);
        if (!providerData || !providerData.id) {
            myEquipmentListDiv.innerHTML = `
                <div class="empty-state">
                    <i class="fas fa-user-times"></i>
                    <h3>Error de proveedor</h3>
                    <p>No se pudo cargar la información del proveedor.</p>
                </div>
            `;
            return;
        }
        
        const providerId = providerData.id;
        const myEquipment = await fetchApi(`/equipment/provider/${providerId}`, 'GET', null, true);
        renderEquipmentList(myEquipment, myEquipmentListDiv, 'provider-dashboard');
    } catch (error) {
        myEquipmentListDiv.innerHTML = `
            <div class="empty-state">
                <i class="fas fa-boxes"></i>
                <h3>Error al cargar equipos</h3>
                <p>No se pudieron cargar tus equipos.</p>
            </div>
        `;
    }
}

async function openEquipmentModalForEdit(equipmentId = null) {
    await populateEquipmentFormDropdowns();
    equipmentModal.classList.remove('hidden');

    if (equipmentId) {
        equipmentModalTitle.innerHTML = '<i class="fas fa-edit"></i> Editar Equipo';
        try {
            const equipment = await fetchApi(`/equipment/${equipmentId}`);
            equipmentIdInput.value = equipment.id;
            equipmentNameInput.value = equipment.name;
            equipmentDescriptionInput.value = equipment.description || '';
            equipmentPriceInput.value = equipment.price;
            equipmentUrlImageInput.value = equipment.urlImage || '';
            equipmentTypeIdSelect.value = equipment.idTypeEquipment;
            equipmentStateIdSelect.value = equipment.idStateEquipment;
            equipmentBrandIdSelect.value = equipment.idBrand;
        } catch (error) {
            showError('Error al cargar datos del equipo para edición');
            equipmentModal.classList.add('hidden');
        }
    } else {
        equipmentModalTitle.innerHTML = '<i class="fas fa-plus"></i> Añadir Equipo';
        equipmentForm.reset();
        equipmentIdInput.value = '';
    }
}

async function populateEquipmentFormDropdowns() {
    try {
        const [types, states] = await Promise.all([
            fetchApi('/types-equipment'),
            fetchApi('/states-equipment')
        ]);
        
        populateSelect(equipmentTypeIdSelect, types, 'id', 'type', 'Selecciona tipo de equipo');
        populateSelect(equipmentStateIdSelect, states, 'id', 'state', 'Selecciona estado del equipo');
        
        // Load only company brands for provider
        if (USER_COMPANY_ID) {
            const companyBrands = await fetchApi(`/companies/${USER_COMPANY_ID}/brands`);
            populateSelect(equipmentBrandIdSelect, companyBrands, 'id', 'nameBrand', 'Selecciona marca');
        }
    } catch (error) {
        showError('No se pudieron cargar las opciones para el equipo');
        equipmentModal.classList.add('hidden');
    }
}

async function handleEquipmentFormSubmit(event) {
    event.preventDefault();
    const equipmentId = equipmentIdInput.value;
    const method = equipmentId ? 'PUT' : 'POST';
    const url = equipmentId ? `/equipment/${equipmentId}` : '/equipment';

    // Validation
    const name = equipmentNameInput.value.trim();
    const price = parseFloat(equipmentPriceInput.value);
    const urlImage = equipmentUrlImageInput.value.trim();

    if (!name || name.length < 2) {
        showError('El nombre del equipo debe tener al menos 2 caracteres');
        return;
    }

    if (isNaN(price) || price <= 0) {
        showError('El precio debe ser un número mayor a 0');
        return;
    }

    if (urlImage && !isValidURL(urlImage)) {
        showError('Por favor ingresa una URL válida para la imagen');
        return;
    }

    if (!equipmentTypeIdSelect.value) {
        showError('Por favor selecciona un tipo de equipo');
        return;
    }

    if (!equipmentStateIdSelect.value) {
        showError('Por favor selecciona un estado del equipo');
        return;
    }

    if (!equipmentBrandIdSelect.value) {
        showError('Por favor selecciona una marca');
        return;
    }

    const equipmentData = {
        name,
        description: equipmentDescriptionInput.value.trim() || null,
        price,
        urlImage: urlImage || null,
        typeId: parseInt(equipmentTypeIdSelect.value),
        stateId: parseInt(equipmentStateIdSelect.value),
        brandId: parseInt(equipmentBrandIdSelect.value)
    };

    try {
        await fetchApi(url, method, equipmentData, true);
        showSuccess(equipmentId ? 'Equipo actualizado con éxito' : 'Equipo añadido con éxito');
        equipmentModal.classList.add('hidden');
        loadMyEquipment();
        loadCatalog(); // Refresh main catalog
    } catch (error) {
        // Error already displayed
    }
}

async function deleteEquipment(equipmentId) {
    if (!confirm('¿Estás seguro de que quieres eliminar este equipo?\n\nEsta acción es irreversible.')) return;
    
    try {
        await fetchApi(`/equipment/${equipmentId}`, 'DELETE', null, true);
        showSuccess('Equipo eliminado con éxito');
        loadMyEquipment();
        loadCatalog();
    } catch (error) {
        // Error already displayed
    }
}

async function loadCompanyDetails() {
    if (!isProvider() || !USER_COMPANY_ID) {
        companyDetailsView.innerHTML = `
            <div class="empty-state">
                <i class="fas fa-building"></i>
                <h3>Sin empresa asociada</h3>
                <p>No hay información de compañía asociada a tu cuenta.</p>
            </div>
        `;
        return;
    }
    
    try {
        const company = await fetchApi(`/companies/${USER_COMPANY_ID}`);
        viewCompanyName.textContent = company.name || 'N/A';
        viewCompanyEmail.textContent = company.email || 'N/A';
        viewCompanyPhone.textContent = company.phone || 'N/A';
        viewCompanyAddress.textContent = company.address || 'N/A';
        viewCompanyWebsite.textContent = company.webSite || 'N/A';
        
        // Make website clickable if it exists
        if (company.webSite) {
            viewCompanyWebsite.innerHTML = `<a href="${company.webSite}" target="_blank">${company.webSite}</a>`;
        }
        
        loadCompanyBrandsForProvider(USER_COMPANY_ID);
    } catch (error) {
        companyDetailsView.innerHTML = `
            <div class="empty-state">
                <i class="fas fa-exclamation-triangle"></i>
                <h3>Error al cargar empresa</h3>
                <p>No se pudo cargar la información de la compañía.</p>
            </div>
        `;
    }
}

async function loadCompanyBrandsForProvider(companyId) {
    try {
        const brands = await fetchApi(`/companies/${companyId}/brands`);
        companyBrandsListDiv.innerHTML = '';
        
        if (brands.length === 0) {
            companyBrandsListDiv.innerHTML = `
                <div class="empty-state">
                    <i class="fas fa-tags"></i>
                    <h3>Sin marcas asociadas</h3>
                    <p>Tu empresa no tiene marcas asociadas.</p>
                </div>
            `;
            return;
        }
        
        brands.forEach((brand, index) => {
            const li = document.createElement('div');
            li.className = 'list-item';
            li.style.animationDelay = `${index * 0.1}s`;
            li.innerHTML = `
                <div class="list-item-content">
                    <span><i class="fas fa-tag"></i> ${brand.nameBrand}</span>
                </div>
            `;
            companyBrandsListDiv.appendChild(li);
        });
    } catch (error) {
        companyBrandsListDiv.innerHTML = `
            <div class="empty-state">
                <i class="fas fa-exclamation-triangle"></i>
                <h3>Error al cargar marcas</h3>
                <p>No se pudieron cargar las marcas asociadas.</p>
            </div>
        `;
    }
}

// --- Brand Management Functions ---

async function openBrandModalForEdit(brandId = null) {
    brandModal.classList.remove('hidden');
    
    if (brandId) {
        brandModalTitle.innerHTML = '<i class="fas fa-edit"></i> Editar Marca';
        try {
            const brand = await fetchApi(`/brands/${brandId}`);
            brandIdInput.value = brand.id;
            brandNameInput.value = brand.nameBrand;
        } catch (error) {
            showError('Error al cargar datos de la marca');
            brandModal.classList.add('hidden');
        }
    } else {
        brandModalTitle.innerHTML = '<i class="fas fa-plus"></i> Añadir Marca';
        brandForm.reset();
        brandIdInput.value = '';
    }
}

async function handleBrandFormSubmit(event) {
    event.preventDefault();
    const brandId = brandIdInput.value;
    const method = brandId ? 'PUT' : 'POST';
    const url = brandId ? `/brands/${brandId}` : '/brands';

    // Validation
    const nameBrand = brandNameInput.value.trim();
    if (!nameBrand || nameBrand.length < 2) {
        showError('El nombre de la marca debe tener al menos 2 caracteres');
        return;
    }

    const brandData = { nameBrand };

    try {
        await fetchApi(url, method, brandData, true);
        showSuccess(brandId ? 'Marca actualizada con éxito' : 'Marca añadida con éxito');
        brandModal.classList.add('hidden');
        loadBrands();
    } catch (error) {
        // Error already displayed
    }
}

async function deleteBrand(brandId) {
    if (!confirm('¿Estás seguro de que quieres eliminar esta marca?\n\nEsta acción es irreversible y puede afectar equipos asociados.')) return;
    
    try {
        await fetchApi(`/brands/${brandId}`, 'DELETE', null, true);
        showSuccess('Marca eliminada con éxito');
        loadBrands();
    } catch (error) {
        // Error already displayed
    }
}

// --- Company Brands Management ---

async function openManageCompanyBrandsModal(companyId) {
    try {
        const company = await fetchApi(`/companies/${companyId}`);
        manageBrandsCompanyName.textContent = company.name;
        manageBrandsCompanyId.value = companyId;
        
        await loadCompanyBrands(companyId);
        await loadAvailableBrandsForAssociation();
        
        manageCompanyBrandsModal.classList.remove('hidden');
    } catch (error) {
        showError('Error al cargar información de la empresa');
    }
}

async function loadCompanyBrands(companyId) {
    try {
        const brands = await fetchApi(`/companies/${companyId}/brands`);
        manageBrandsListDiv.innerHTML = '';
        
        if (brands.length === 0) {
            manageBrandsListDiv.innerHTML = `
                <div class="empty-state">
                    <i class="fas fa-tags"></i>
                    <h3>Sin marcas asociadas</h3>
                    <p>Esta empresa no tiene marcas asociadas.</p>
                </div>
            `;
            return;
        }
        
        brands.forEach((brand, index) => {
            const item = document.createElement('div');
            item.className = 'brand-item';
            item.style.animationDelay = `${index * 0.1}s`;
            item.innerHTML = `
                <span><i class="fas fa-tag"></i> ${brand.nameBrand}</span>
                <button class="btn btn-danger delete-btn" data-brand-id="${brand.id}" data-company-id="${companyId}">
                    <i class="fas fa-unlink"></i> Desasociar
                </button>
            `;
            
            item.querySelector('.delete-btn').addEventListener('click', (e) => {
                removeCompanyBrand(e.currentTarget.dataset.companyId, e.currentTarget.dataset.brandId);
            });
            
            manageBrandsListDiv.appendChild(item);
        });
    } catch (error) {
        manageBrandsListDiv.innerHTML = `
            <div class="empty-state">
                <i class="fas fa-exclamation-triangle"></i>
                <h3>Error al cargar marcas</h3>
                <p>No se pudieron cargar las marcas asociadas.</p>
            </div>
        `;
    }
}

async function loadAvailableBrandsForAssociation() {
    try {
        const allBrands = await fetchApi('/brands');
        populateSelect(selectBrandToAssociate, allBrands, 'id', 'nameBrand', 'Selecciona una marca');
    } catch (error) {
        showError('No se pudieron cargar las marcas disponibles');
    }
}

async function handleAssociateBrandFormSubmit(event) {
    event.preventDefault();
    const companyId = manageBrandsCompanyId.value;
    const brandId = selectBrandToAssociate.value;

    if (!brandId) {
        showError('Por favor, selecciona una marca');
        return;
    }

    try {
        await fetchApi(`/companies/${companyId}/brands/${brandId}`, 'POST', null, true);
        showSuccess('Marca asociada con éxito');
        loadCompanyBrands(companyId);
        associateBrandForm.reset();
    } catch (error) {
        // Error already displayed
    }
}

async function removeCompanyBrand(companyId, brandId) {
    if (!confirm('¿Estás seguro de que quieres desasociar esta marca de la empresa?')) return;
    
    try {
        await fetchApi(`/companies/${companyId}/brands/${brandId}`, 'DELETE', null, true);
        showSuccess('Marca desasociada con éxito');
        loadCompanyBrands(companyId);
    } catch (error) {
        // Error already displayed
    }
}

// --- Provider Dashboard Tab Management ---
providerTabs.forEach(tab => {
    tab.addEventListener('click', (e) => {
        document.querySelectorAll('#provider-dashboard-page .tab-content').forEach(content => 
            content.classList.add('hidden')
        );
        document.querySelectorAll('#provider-dashboard-page .tab-button').forEach(btn => 
            btn.classList.remove('active')
        );
        document.getElementById(`${e.target.dataset.tab}-tab`).classList.remove('hidden');
        e.target.classList.add('active');

        if (e.target.dataset.tab === 'provider-equipment') loadMyEquipment();
        else if (e.target.dataset.tab === 'provider-company') loadCompanyDetails();
    });
});

// --- Client Dashboard Tab Management ---
clientTabs.forEach(tab => {
    tab.addEventListener('click', (e) => {
        document.querySelectorAll('#client-dashboard-page .tab-content').forEach(content => 
            content.classList.add('hidden')
        );
        document.querySelectorAll('#client-dashboard-page .tab-button').forEach(btn => 
            btn.classList.remove('active')
        );
        document.getElementById(`${e.target.dataset.tab}-tab`).classList.remove('hidden');
        e.target.classList.add('active');

        if (e.target.dataset.tab === 'client-favorites') loadMyFavorites();
        else if (e.target.dataset.tab === 'client-reviews') loadMyReviews();
    });
});

// --- Additional Event Listeners ---

// Equipment form submit
addEquipmentBtn?.addEventListener('click', () => openEquipmentModalForEdit(null));
equipmentForm?.addEventListener('submit', handleEquipmentFormSubmit);

// Brand form submit
brandForm?.addEventListener('submit', handleBrandFormSubmit);

// Associate brand form submit
associateBrandForm?.addEventListener('submit', handleAssociateBrandFormSubmit);

// --- Star Rating System ---
document.addEventListener('DOMContentLoaded', function() {
    const starInputs = document.querySelectorAll('.star-rating-input');
    starInputs.forEach(function(starInput) {
        const stars = starInput.querySelectorAll('.star');
        const hiddenInput = starInput.querySelector('input[type="number"]');
        
        stars.forEach(function(star, idx) {
            star.addEventListener('click', function() {
                stars.forEach((s, i) => {
                    if (i <= idx) {
                        s.classList.add('selected');
                    } else {
                        s.classList.remove('selected');
                    }
                });
                hiddenInput.value = idx + 1;
            });
            
            star.addEventListener('mouseover', function() {
                stars.forEach((s, i) => {
                    if (i <= idx) {
                        s.classList.add('hovered');
                    } else {
                        s.classList.remove('hovered');
                    }
                });
            });
            
            star.addEventListener('mouseout', function() {
                stars.forEach((s, i) => {
                    s.classList.remove('hovered');
                });
            });
        });
    });
});

// --- Initialize Application ---
document.addEventListener('DOMContentLoaded', init);