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

// --- Helper Functions ---

function showLoader() {
    loadingSpinner.classList.remove('hidden');
}

function hideLoader() {
    loadingSpinner.classList.add('hidden');
}

function showError(message) {
    errorMessageDiv.textContent = message;
    errorMessageDiv.classList.remove('hidden');
    setTimeout(() => {
        errorMessageDiv.classList.add('hidden');
    }, 5000);
}

function showPage(pageId) {
    pages.forEach(page => page.classList.add('hidden'));
    const targetPage = document.getElementById(pageId);
    if (targetPage) {
        targetPage.classList.remove('hidden');
    }
    navButtons.forEach(btn => btn.classList.remove('active'));
    const navBtnId = `nav-${pageId.replace('-page', '')}`;
    const navBtnToActivate = document.getElementById(navBtnId);
    if (navBtnToActivate) {
        navBtnToActivate.classList.add('active');
    }
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
    const email = document.getElementById('login-email').value;
    const password = document.getElementById('login-password').value;

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

        alert('Inicio de sesión exitoso!');
        loginForm.reset();
        updateAuthUI();
        
        // Redirigir según el rol
        if (USER_ROLE === 'administrador') {
            showPage('admin-dashboard-page');
            loadAdminDashboard();
        } else if (USER_ROLE === 'proveedor') {
            showPage('provider-dashboard-page');
            loadProviderDashboard();
        } else if (USER_ROLE === 'cliente') {
            showPage('catalog-page');
            loadCatalog();
        }
    } catch (error) {
        // Error already displayed by fetchApi
    }
}

async function handleRegister(event) {
    event.preventDefault();
    const fullName = document.getElementById('register-fullname').value;
    const email = document.getElementById('register-email').value;
    const password = document.getElementById('register-password').value;
    const userType = registerUserTypeSelect.value;

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

        alert('Registro exitoso!');
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
    localStorage.removeItem('userToken');
    localStorage.removeItem('currentUserId');
    localStorage.removeItem('userRole');
    localStorage.removeItem('userCompanyId');

    CURRENT_USER = null;
    USER_TOKEN = null;
    USER_ROLE = null;
    USER_COMPANY_ID = null;

    alert('Sesión cerrada.');
    updateAuthUI();
    showPage('catalog-page');
    loadCatalog();
}

function updateAuthUI() {
    updateNavButtons();
    loginForm.reset();
    registerForm.reset();
    registerUserTypeSelect.value = '';
    providerFieldsDiv.classList.add('hidden');

    if (isClient()) {
        reviewFormContainer.classList.remove('hidden');
        reviewLoginPrompt.classList.add('hidden');
    } else {
        reviewFormContainer.classList.add('hidden');
        reviewLoginPrompt.classList.remove('hidden');
    }
}

// --- Catalog Functions ---

async function loadCatalog() {
    try {
        const equipment = await fetchApi('/equipment');
        renderEquipmentList(equipment, equipmentListDiv, 'catalog');
        await loadFilterOptions();
    } catch (error) {
        equipmentListDiv.innerHTML = '<p>Error al cargar el catálogo.</p>';
    }
}

async function loadFilterOptions() {
    try {
        // Cargar tipos
        const types = await fetchApi('/types-equipment');
        populateSelect(filterTypeSelect, types, 'id', 'type', 'Todos los tipos');
        
        // Cargar estados
        const states = await fetchApi('/states-equipment');
        populateSelect(filterStateSelect, states, 'id', 'state', 'Todos los estados');
        
        // Cargar empresas
        const companies = await fetchApi('/companies');
        populateSelect(filterCompanySelect, companies, 'id', 'name', 'Todas las empresas');
        
        // Cargar marcas
        const brands = await fetchApi('/brands');
        populateSelect(filterBrandSelect, brands, 'id', 'nameBrand', 'Todas las marcas');
    } catch (error) {
        console.error('Error cargando opciones de filtro:', error);
    }
}

async function applyFilters() {
    const name = filterNameInput.value;
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
        container.innerHTML = '<p>No hay equipos disponibles.</p>';
        return;
    }

    equipmentList.forEach(equipment => {
        const card = document.createElement('div');
        card.className = 'card';
        card.innerHTML = `
            <img src="${equipment.urlImage || 'https://via.placeholder.com/150'}" alt="${equipment.name}">
            <h3>${equipment.name}</h3>
            <p><strong>Marca:</strong> ${equipment.brandName}</p>
            <p><strong>Tipo:</strong> ${equipment.typeName}</p>
            <p><strong>Estado:</strong> ${equipment.stateName}</p>
            <p><strong>Precio:</strong> $${equipment.price.toFixed(2)}</p>
            <p><strong>Empresa:</strong> ${equipment.companyName}</p>
            ${context === 'catalog' || context === 'provider-dashboard' ? 
                '<button class="btn view-detail-btn" data-id="' + equipment.id + '">Ver Detalles</button>' : ''}
            <div class="card-buttons">
                ${context === 'provider-dashboard' ? `
                    <button class="btn edit-btn" data-id="${equipment.id}">Editar</button>
                    <button class="btn delete-btn" data-id="${equipment.id}">Eliminar</button>
                ` : ''}
                ${context === 'client-favorites' ? `
                    <button class="btn delete-btn" data-favorite-id="${equipment.favoriteId}">Quitar de Favoritos</button>
                ` : ''}
            </div>
        `;

        if (context === 'catalog' || context === 'provider-dashboard') {
            card.querySelector('.view-detail-btn').addEventListener('click', (e) => {
                const equipmentId = e.target.dataset.id;
                loadEquipmentDetail(equipmentId);
            });
        }
        if (context === 'provider-dashboard') {
            card.querySelector('.edit-btn').addEventListener('click', (e) => {
                openEquipmentModalForEdit(e.target.dataset.id);
            });
            card.querySelector('.delete-btn').addEventListener('click', (e) => {
                deleteEquipment(e.target.dataset.id);
            });
        }
        if (context === 'client-favorites') {
            card.querySelector('.delete-btn').addEventListener('click', (e) => {
                removeFavorite(e.target.dataset.favoriteId);
            });
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
        showPage('catalog-page');
    }
}

function renderEquipmentDetail(equipment) {
    equipmentDetailContentDiv.innerHTML = `
        <img id="equipment-detail-image" src="${equipment.urlImage || 'https://via.placeholder.com/400'}" alt="${equipment.name}">
        <div class="detail-info">
            <h2>${equipment.name}</h2>
            <p><strong>Descripción:</strong> ${equipment.description || 'Sin descripción.'}</p>
            <p><strong>Precio:</strong> $${equipment.price.toFixed(2)}</p>
            <p><strong>Marca:</strong> ${equipment.brandName}</p>
            <p><strong>Tipo:</strong> ${equipment.typeName}</p>
            <p><strong>Estado:</strong> ${equipment.stateName}</p>
            <p><strong>Proveedor:</strong> ${equipment.providerName}</p>
            <p><strong>Empresa:</strong> ${equipment.companyName || 'N/A'}</p>
            <p>Publicado el: ${new Date(equipment.createdAt).toLocaleDateString()}</p>
            ${isClient() ? `<button id="add-to-favorites-btn" class="btn" data-id="${equipment.id}">Añadir a Favoritos</button>` : ''}
        </div>
    `;

    if (isClient()) {
        document.getElementById('add-to-favorites-btn').addEventListener('click', (e) => {
            addToFavorites(e.target.dataset.id);
        });
    }

    reviewStarValueInput.value = '';
    reviewBodyInput.value = '';
    submitReviewBtn.dataset.equipmentId = equipment.id;
    updateAuthUI();
}

// --- Review Functions ---

async function loadReviewsForEquipment(equipmentId) {
    try {
        const reviews = await fetchApi(`/equipment/${equipmentId}/reviews`);
        renderReviewsList(reviews, reviewsListDiv, 'equipment');
    } catch (error) {
        reviewsListDiv.innerHTML = '<p>Error al cargar las reseñas.</p>';
    }
}

async function submitReview(event) {
    event.preventDefault();
    const equipmentId = parseInt(submitReviewBtn.dataset.equipmentId);
    const starValue = parseInt(reviewStarValueInput.value);
    const body = reviewBodyInput.value;

    if (!starValue || starValue < 1 || starValue > 5) {
        showError('Por favor, selecciona un valor de estrella entre 1 y 5.');
        return;
    }

    try {
        await fetchApi('/reviews', 'POST', { equipmentId, starValue, body }, true);
        alert('Reseña enviada con éxito!');
        reviewStarValueInput.value = '';
        reviewBodyInput.value = '';
        loadReviewsForEquipment(equipmentId);
        if (isClient()) loadMyReviews();
    } catch (error) {
        // Error already displayed
    }
}

function renderReviewsList(reviews, container, context = 'equipment') {
    container.innerHTML = '';
    if (reviews.length === 0) {
        container.innerHTML = '<p>No hay reseñas todavía.</p>';
        return;
    }

    reviews.forEach(review => {
        const reviewItem = document.createElement('div');
        reviewItem.className = 'review-item';
        reviewItem.innerHTML = `
            <div>
                <p class="review-meta"><strong>${review.userName}</strong> - ${review.starValue} estrellas (${new Date(review.createdAt).toLocaleDateString()})</p>
                <p class="review-body">${review.body || 'Sin comentario.'}</p>
                ${context === 'client-dashboard' ? `<p><strong>Equipo:</strong> ${review.equipmentName}</p>` : ''}
            </div>
            ${context === 'client-dashboard' ? `
                <button class="btn delete-btn" data-id="${review.id}">Eliminar</button>
            ` : ''}
        `;
        if (context === 'client-dashboard') {
            reviewItem.querySelector('.delete-btn').addEventListener('click', (e) => {
                deleteReview(e.target.dataset.id);
            });
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
        myReviewsListDiv.innerHTML = '<p>Error al cargar mis reseñas.</p>';
    }
}

async function deleteReview(reviewId) {
    if (!confirm('¿Estás seguro de que quieres eliminar esta reseña?')) return;
    try {
        await fetchApi(`/reviews/${reviewId}`, 'DELETE', null, true);
        alert('Reseña eliminada con éxito!');
        const currentEquipmentId = submitReviewBtn.dataset.equipmentId;
        if (currentEquipmentId) loadReviewsForEquipment(currentEquipmentId);
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
        alert('Equipo añadido a favoritos!');
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
        alert('Favorito eliminado con éxito!');
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
        myFavoritesListDiv.innerHTML = '<p>Error al cargar mis favoritos.</p>';
    }
}

// --- Admin Dashboard Functions ---

async function loadAdminDashboard() {
    if (!isAdmin()) {
        showError('Acceso denegado. Solo administradores.');
        showPage('catalog-page');
        return;
    }
    showPage('admin-dashboard-page');
    loadCompanies();
    loadBrands();
}

async function loadCompanies() {
    try {
        const companies = await fetchApi('/companies');
        renderCompaniesList(companies);
    } catch (error) {
        companiesListDiv.innerHTML = '<p>Error al cargar empresas.</p>';
    }
}

function renderCompaniesList(companies) {
    companiesListDiv.innerHTML = '';
    if (companies.length === 0) {
        companiesListDiv.innerHTML = '<p>No hay empresas registradas.</p>';
        return;
    }

    companies.forEach(company => {
        const item = document.createElement('div');
        item.className = 'list-item';
        item.innerHTML = `
            <div class="list-item-content">
                <h4>${company.name}</h4>
                <p>${company.email || 'Sin email'} | ${company.phone || 'Sin teléfono'}</p>
                <p>${company.address || 'Sin dirección'}</p>
            </div>
            <div class="list-item-buttons">
                <button class="btn" data-id="${company.id}">Gestionar Marcas</button>
                <button class="btn edit-btn" data-id="${company.id}">Editar</button>
                <button class="btn delete-btn" data-id="${company.id}">Eliminar</button>
            </div>
        `;
        
        item.querySelector('.btn:not(.edit-btn):not(.delete-btn)').addEventListener('click', (e) => {
            openManageCompanyBrandsModal(e.target.dataset.id);
        });
        item.querySelector('.edit-btn').addEventListener('click', (e) => {
            openCompanyModalForEdit(e.target.dataset.id);
        });
        item.querySelector('.delete-btn').addEventListener('click', (e) => {
            deleteCompany(e.target.dataset.id);
        });
        
        companiesListDiv.appendChild(item);
    });
}

async function loadBrands() {
    try {
        const brands = await fetchApi('/brands');
        renderBrandsList(brands);
    } catch (error) {
        brandsListDiv.innerHTML = '<p>Error al cargar marcas.</p>';
    }
}

function renderBrandsList(brands) {
    brandsListDiv.innerHTML = '';
    if (brands.length === 0) {
        brandsListDiv.innerHTML = '<p>No hay marcas registradas.</p>';
        return;
    }

    brands.forEach(brand => {
        const item = document.createElement('div');
        item.className = 'list-item';
        item.innerHTML = `
            <div class="list-item-content">
                <span>${brand.nameBrand}</span>
            </div>
            <div class="list-item-buttons">
                <button class="btn edit-btn" data-id="${brand.id}">Editar</button>
                <button class="btn delete-btn" data-id="${brand.id}">Eliminar</button>
            </div>
        `;
        
        item.querySelector('.edit-btn').addEventListener('click', (e) => {
            openBrandModalForEdit(e.target.dataset.id);
        });
        item.querySelector('.delete-btn').addEventListener('click', (e) => {
            deleteBrand(e.target.dataset.id);
        });
        
        brandsListDiv.appendChild(item);
    });
}

// Company Management
async function openCompanyModalForEdit(companyId = null) {
    companyModal.classList.remove('hidden');
    
    if (companyId) {
        companyModalTitle.textContent = 'Editar Empresa';
        try {
            const company = await fetchApi(`/companies/${companyId}`);
            companyIdInput.value = company.id;
            companyNameInput.value = company.name;
            companyEmailInput.value = company.email || '';
            companyPhoneInput.value = company.phone || '';
            companyAddressInput.value = company.address || '';
            companyWebsiteInput.value = company.webSite || '';
        } catch (error) {
            alert('Error al cargar datos de la empresa.');
            companyModal.classList.add('hidden');
        }
    } else {
        companyModalTitle.textContent = 'Añadir Empresa';
        companyForm.reset();
        companyIdInput.value = '';
    }
}

async function handleCompanyFormSubmit(event) {
    event.preventDefault();
    const companyId = companyIdInput.value;
    const method = companyId ? 'PUT' : 'POST';
    const url = companyId ? `/companies/${companyId}` : '/companies';

    const companyData = {
        name: companyNameInput.value,
        email: companyEmailInput.value,
        phone: companyPhoneInput.value,
        address: companyAddressInput.value,
        webSite: companyWebsiteInput.value
    };

    if (companyId) {
        companyData.id = parseInt(companyId);
    }

    try {
        await fetchApi(url, method, companyData, true);
        alert(companyId ? 'Empresa actualizada con éxito!' : 'Empresa añadida con éxito!');
        companyModal.classList.add('hidden');
        loadCompanies();
        // Recargar las opciones de empresa en el formulario de registro
        loadCompaniesForRegister();
    } catch (error) {
        // Error already displayed
    }
}

async function deleteCompany(companyId) {
    if (!confirm('¿Estás seguro de que quieres eliminar esta empresa? Esta acción es irreversible.')) return;
    try {
        await fetchApi(`/companies/${companyId}`, 'DELETE', null, true);
        alert('Empresa eliminada con éxito!');
        loadCompanies();
    } catch (error) {
        // Error already displayed
    }
}

// Company Brands Management
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
            manageBrandsListDiv.innerHTML = '<p>No hay marcas asociadas a esta empresa.</p>';
            return;
        }
        
        brands.forEach(brand => {
            const item = document.createElement('div');
            item.className = 'brand-item';
            item.innerHTML = `
                <span>${brand.nameBrand}</span>
                <button class="btn delete-btn" data-brand-id="${brand.id}" data-company-id="${companyId}">Desasociar</button>
            `;
            
            item.querySelector('.delete-btn').addEventListener('click', (e) => {
                removeCompanyBrand(e.target.dataset.companyId, e.target.dataset.brandId);
            });
            
            manageBrandsListDiv.appendChild(item);
        });
    } catch (error) {
        manageBrandsListDiv.innerHTML = '<p>Error al cargar marcas asociadas.</p>';
    }
}

async function loadAvailableBrandsForAssociation() {
    try {
        const allBrands = await fetchApi('/brands');
        populateSelect(selectBrandToAssociate, allBrands, 'id', 'nameBrand', 'Selecciona una marca');
    } catch (error) {
        showError('No se pudieron cargar las marcas disponibles.');
    }
}

async function handleAssociateBrandFormSubmit(event) {
    event.preventDefault();
    const companyId = manageBrandsCompanyId.value;
    const brandId = selectBrandToAssociate.value;

    if (!brandId) {
        showError('Por favor, selecciona una marca.');
        return;
    }

    try {
        await fetchApi(`/companies/${companyId}/brands/${brandId}`, 'POST', null, true);
        alert('Marca asociada con éxito!');
        loadCompanyBrands(companyId);
        associateBrandForm.reset();
    } catch (error) {
        // Error already displayed
    }
}

async function removeCompanyBrand(companyId, brandId) {
    if (!confirm('¿Estás seguro de que quieres desasociar esta marca?')) return;
    try {
        await fetchApi(`/companies/${companyId}/brands/${brandId}`, 'DELETE', null, true);
        alert('Marca desasociada con éxito!');
        loadCompanyBrands(companyId);
    } catch (error) {
        // Error already displayed
    }
}

// Brand Management
async function openBrandModalForEdit(brandId = null) {
    brandModal.classList.remove('hidden');
    
    if (brandId) {
        brandModalTitle.textContent = 'Editar Marca';
        try {
            const brand = await fetchApi(`/brands/${brandId}`);
            brandIdInput.value = brand.id;
            brandNameInput.value = brand.nameBrand;
        } catch (error) {
            alert('Error al cargar datos de la marca.');
            brandModal.classList.add('hidden');
        }
    } else {
        brandModalTitle.textContent = 'Añadir Marca';
        brandForm.reset();
        brandIdInput.value = '';
    }
}

async function handleBrandFormSubmit(event) {
    event.preventDefault();
    const brandId = brandIdInput.value;
    const method = brandId ? 'PUT' : 'POST';
    const url = brandId ? `/brands/${brandId}` : '/brands';

    const brandData = {
        nameBrand: brandNameInput.value
    };

    try {
        await fetchApi(url, method, brandData, true);
        alert(brandId ? 'Marca actualizada con éxito!' : 'Marca añadida con éxito!');
        brandModal.classList.add('hidden');
        loadBrands();
    } catch (error) {
        // Error already displayed
    }
}

async function deleteBrand(brandId) {
    if (!confirm('¿Estás seguro de que quieres eliminar esta marca? Esta acción es irreversible.')) return;
    try {
        await fetchApi(`/brands/${brandId}`, 'DELETE', null, true);
        alert('Marca eliminada con éxito!');
        loadBrands();
    } catch (error) {
        // Error already displayed
    }
}

// --- Provider Dashboard Functions ---

async function loadProviderDashboard() {
    if (!isProvider()) {
        showError('Acceso denegado. Solo proveedores.');
        showPage('catalog-page');
        return;
    }
    showPage('provider-dashboard-page');
    loadMyEquipment();
    loadCompanyDetails();
}

async function loadMyEquipment() {
    if (!isProvider()) return;
    try {
        const providerData = await fetchApi(`/users/${CURRENT_USER}/provider`, 'GET', null, true);
        if (!providerData || !providerData.id) {
            myEquipmentListDiv.innerHTML = '<p>No se pudo cargar la información del proveedor.</p>';
            return;
        }
        const providerId = providerData.id;
        const myEquipment = await fetchApi(`/equipment/provider/${providerId}`, 'GET', null, true);
        renderEquipmentList(myEquipment, myEquipmentListDiv, 'provider-dashboard');
    } catch (error) {
        myEquipmentListDiv.innerHTML = '<p>Error al cargar mis equipos.</p>';
    }
}

async function openEquipmentModalForEdit(equipmentId = null) {
    await populateEquipmentFormDropdowns();
    equipmentModal.classList.remove('hidden');

    if (equipmentId) {
        equipmentModalTitle.textContent = 'Editar Equipo';
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
            alert('Error al cargar datos del equipo para edición.');
            equipmentModal.classList.add('hidden');
        }
    } else {
        equipmentModalTitle.textContent = 'Añadir Equipo';
        equipmentForm.reset();
        equipmentIdInput.value = '';
    }
}

async function populateEquipmentFormDropdowns() {
    try {
        // Cargar tipos de equipo
        const types = await fetchApi('/types-equipment');
        populateSelect(equipmentTypeIdSelect, types, 'id', 'type', 'Selecciona tipo de equipo');
        
        // Cargar estados de equipo
        const states = await fetchApi('/states-equipment');
        populateSelect(equipmentStateIdSelect, states, 'id', 'state', 'Selecciona estado del equipo');
        
        // Cargar solo las marcas de la empresa del proveedor
        if (USER_COMPANY_ID) {
            const companyBrands = await fetchApi(`/companies/${USER_COMPANY_ID}/brands`);
            populateSelect(equipmentBrandIdSelect, companyBrands, 'id', 'nameBrand', 'Selecciona marca');
        }
    } catch (error) {
        showError('No se pudieron cargar las opciones para el equipo.');
        equipmentModal.classList.add('hidden');
    }
}

async function handleEquipmentFormSubmit(event) {
    event.preventDefault();
    const equipmentId = equipmentIdInput.value;
    const method = equipmentId ? 'PUT' : 'POST';
    const url = equipmentId ? `/equipment/${equipmentId}` : '/equipment';

    const equipmentData = {
        name: equipmentNameInput.value,
        description: equipmentDescriptionInput.value,
        price: parseFloat(equipmentPriceInput.value),
        urlImage: equipmentUrlImageInput.value,
        typeId: parseInt(equipmentTypeIdSelect.value),
        stateId: parseInt(equipmentStateIdSelect.value),
        brandId: parseInt(equipmentBrandIdSelect.value)
    };

    try {
        await fetchApi(url, method, equipmentData, true);
        alert(equipmentId ? 'Equipo actualizado con éxito!' : 'Equipo añadido con éxito!');
        equipmentModal.classList.add('hidden');
        loadMyEquipment();
        loadCatalog(); // Refresh main catalog
    } catch (error) {
        // Error already displayed
    }
}

async function deleteEquipment(equipmentId) {
    if (!confirm('¿Estás seguro de que quieres eliminar este equipo? Esta acción es irreversible.')) return;
    try {
        await fetchApi(`/equipment/${equipmentId}`, 'DELETE', null, true);
        alert('Equipo eliminado con éxito!');
        loadMyEquipment();
        loadCatalog();
    } catch (error) {
        // Error already displayed
    }
}

async function loadCompanyDetails() {
    if (!isProvider() || !USER_COMPANY_ID) {
        companyDetailsView.innerHTML = '<p>No hay información de compañía asociada.</p>';
        return;
    }
    try {
        const company = await fetchApi(`/companies/${USER_COMPANY_ID}`);
        viewCompanyName.textContent = company.name;
        viewCompanyEmail.textContent = company.email || 'N/A';
        viewCompanyPhone.textContent = company.phone || 'N/A';
        viewCompanyAddress.textContent = company.address || 'N/A';
        viewCompanyWebsite.textContent = company.webSite || 'N/A';
        
        loadCompanyBrandsForProvider(USER_COMPANY_ID);
    } catch (error) {
        companyDetailsView.innerHTML = '<p>Error al cargar la información de la compañía.</p>';
    }
}

async function loadCompanyBrandsForProvider(companyId) {
    try {
        const brands = await fetchApi(`/companies/${companyId}/brands`);
        companyBrandsListDiv.innerHTML = '';
        if (brands.length === 0) {
            companyBrandsListDiv.innerHTML = '<p>No hay marcas asociadas.</p>';
            return;
        }
        brands.forEach(brand => {
            const li = document.createElement('div');
            li.className = 'list-item';
            li.innerHTML = `<div class="list-item-content"><span>${brand.nameBrand}</span></div>`;
            companyBrandsListDiv.appendChild(li);
        });
    } catch (error) {
        companyBrandsListDiv.innerHTML = '<p>Error al cargar marcas asociadas.</p>';
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

// --- Event Listeners ---

// Navigation
navCatalogBtn.addEventListener('click', () => { showPage('catalog-page'); loadCatalog(); });
navLoginBtn.addEventListener('click', () => showPage('auth-page'));
navLogoutBtn.addEventListener('click', handleLogout);
navAdminDashboardBtn.addEventListener('click', loadAdminDashboard);
navProviderDashboardBtn.addEventListener('click', loadProviderDashboard);
navClientDashboardBtn.addEventListener('click', () => { 
    showPage('client-dashboard-page'); 
    loadMyFavorites(); 
});
backToCatalogBtn.addEventListener('click', () => { 
    showPage('catalog-page'); 
    loadCatalog(); 
});

// Auth Forms
loginForm.addEventListener('submit', handleLogin);
registerForm.addEventListener('submit', handleRegister);
registerUserTypeSelect.addEventListener('change', async (e) => {
    if (e.target.value === 'proveedor') {
        providerFieldsDiv.classList.remove('hidden');
        await loadCompaniesForRegister();
    } else {
        providerFieldsDiv.classList.add('hidden');
    }
});

// Catalog Filters
applyFiltersBtn.addEventListener('click', applyFilters);
clearFiltersBtn.addEventListener('click', clearFilters);

// Review Submit
submitReviewBtn.addEventListener('click', submitReview);

// Admin Dashboard
adminTabs.forEach(tab => {
    tab.addEventListener('click', (e) => {
        document.querySelectorAll('#admin-dashboard-page .tab-content').forEach(content => content.classList.add('hidden'));
        document.querySelectorAll('#admin-dashboard-page .tab-button').forEach(btn => btn.classList.remove('active'));
        document.getElementById(`${e.target.dataset.tab}-tab`).classList.remove('hidden');
        e.target.classList.add('active');

        if (e.target.dataset.tab === 'admin-companies') loadCompanies();
        else if (e.target.dataset.tab === 'admin-brands') loadBrands();
    });
});

addCompanyBtn.addEventListener('click', () => openCompanyModalForEdit(null));
companyForm.addEventListener('submit', handleCompanyFormSubmit);

addBrandBtn.addEventListener('click', () => openBrandModalForEdit(null));
brandForm.addEventListener('submit', handleBrandFormSubmit);

associateBrandForm.addEventListener('submit', handleAssociateBrandFormSubmit);

// Provider Dashboard
providerTabs.forEach(tab => {
    tab.addEventListener('click', (e) => {
        document.querySelectorAll('#provider-dashboard-page .tab-content').forEach(content => content.classList.add('hidden'));
        document.querySelectorAll('#provider-dashboard-page .tab-button').forEach(btn => btn.classList.remove('active'));
        document.getElementById(`${e.target.dataset.tab}-tab`).classList.remove('hidden');
        e.target.classList.add('active');

        if (e.target.dataset.tab === 'provider-equipment') loadMyEquipment();
        else if (e.target.dataset.tab === 'provider-company') loadCompanyDetails();
    });
});

addEquipmentBtn.addEventListener('click', () => openEquipmentModalForEdit(null));
equipmentForm.addEventListener('submit', handleEquipmentFormSubmit);

// Client Dashboard
clientTabs.forEach(tab => {
    tab.addEventListener('click', (e) => {
        document.querySelectorAll('#client-dashboard-page .tab-content').forEach(content => content.classList.add('hidden'));
        document.querySelectorAll('#client-dashboard-page .tab-button').forEach(btn => btn.classList.remove('active'));
        document.getElementById(`${e.target.dataset.tab}-tab`).classList.remove('hidden');
        e.target.classList.add('active');

        if (e.target.dataset.tab === 'client-favorites') loadMyFavorites();
        else if (e.target.dataset.tab === 'client-reviews') loadMyReviews();
    });
});

// --- Initialization ---
function init() {
    // Cargar datos de localStorage
    const token = localStorage.getItem('userToken');
    const userId = localStorage.getItem('currentUserId');
    const role = localStorage.getItem('userRole');
    const companyId = localStorage.getItem('userCompanyId');
    
    // Solo asignar si existen valores válidos
    if (token && userId && role) {
        USER_TOKEN = token;
        CURRENT_USER = userId;
        USER_ROLE = role;
        USER_COMPANY_ID = companyId || null;
    } else {
        // Limpiar si no hay sesión válida
        USER_TOKEN = null;
        CURRENT_USER = null;
        USER_ROLE = null;
        USER_COMPANY_ID = null;
    }

    updateAuthUI();
    
    // Si es admin y está logueado, mostrar el panel de admin
    if (isAdmin()) {
        showPage('admin-dashboard-page');
        loadAdminDashboard();
    } else {
        showPage('catalog-page');
        loadCatalog();
    }
}

document.addEventListener('DOMContentLoaded', init);