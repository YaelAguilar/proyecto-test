// --- Configuración Global ---
const API_BASE_URL = 'http://localhost:7000/api'; // Asegúrate de que coincida con el puerto de tu Javalin
let CURRENT_USER = null;
let USER_TOKEN = null;
let USER_ROLE = null;
let USER_COMPANY_ID = null; // Para proveedores

// --- Referencias a Elementos del DOM ---
const pages = document.querySelectorAll('.page');
const navButtons = document.querySelectorAll('.nav-button');
const loadingSpinner = document.getElementById('loading-spinner');
const errorMessageDiv = document.getElementById('error-message');

// Navigation Buttons
const navCatalogBtn = document.getElementById('nav-catalog');
const navLoginBtn = document.getElementById('nav-auth'); // CORREGIDO: Usar el ID correcto del botón
const navProviderDashboardBtn = document.getElementById('nav-provider-dashboard');
const navClientDashboardBtn = document.getElementById('nav-client-dashboard');
const navLogoutBtn = document.getElementById('nav-logout');

// Auth Page Elements
const loginForm = document.getElementById('login-form');
const registerForm = document.getElementById('register-form');
const registerUserTypeSelect = document.getElementById('register-usertype');
const providerFieldsDiv = document.getElementById('provider-fields');

// Catalog Page Elements
const equipmentListDiv = document.getElementById('equipment-list');

// Equipment Detail Page Elements
const backToCatalogBtn = document.getElementById('back-to-catalog');
const equipmentDetailContentDiv = document.getElementById('equipment-detail-content');
const reviewsListDiv = document.getElementById('reviews-list');
const reviewFormContainer = document.getElementById('review-form-container');
const reviewLoginPrompt = document.getElementById('review-login-prompt');
const reviewStarValueInput = document.getElementById('review-star-value');
const reviewBodyInput = document.getElementById('review-body');
const submitReviewBtn = document.getElementById('submit-review-btn');

// Provider Dashboard Elements
const providerTabs = document.querySelectorAll('#provider-dashboard-page .tab-button');
const providerEquipmentTab = document.getElementById('provider-equipment-tab');
const providerCompanyTab = document.getElementById('provider-company-tab');
const addEquipmentBtn = document.getElementById('add-equipment-btn');
const myEquipmentListDiv = document.getElementById('my-equipment-list');
const companyDetailsView = document.getElementById('company-details-view');
const editCompanyBtn = document.getElementById('edit-company-btn');
const editCompanyForm = document.getElementById('edit-company-form');
const cancelEditCompanyBtn = document.getElementById('cancel-edit-company-btn');
const companyBrandsListDiv = document.getElementById('company-brands-list');
const addCompanyBrandBtn = document.getElementById('add-company-brand-btn');

// Client Dashboard Elements
const clientTabs = document.querySelectorAll('#client-dashboard-page .tab-button');
const clientFavoritesTab = document.getElementById('client-favorites-tab');
const clientReviewsTab = document.getElementById('client-reviews-tab');
const myFavoritesListDiv = document.getElementById('my-favorites-list');
const myReviewsListDiv = document.getElementById('my-reviews-list');

// Modals
const equipmentModal = document.getElementById('equipment-modal');
const equipmentModalTitle = document.getElementById('equipment-modal-title');
const equipmentForm = document.getElementById('equipment-form');
const equipmentTypeIdSelect = document.getElementById('equipment-type-id');
const equipmentStateIdSelect = document.getElementById('equipment-state-id');
const equipmentBrandIdSelect = document.getElementById('equipment-brand-id');
// CORREGIDO: Referencia al input de la URL de la imagen del equipo
const equipmentUrlImageInput = document.getElementById('equipment-url-image'); 

const brandModal = document.getElementById('brand-modal');
const brandModalTitle = document.getElementById('brand-modal-title');
const brandForm = document.getElementById('brand-form');

const typeModal = document.getElementById('type-modal');
const typeModalTitle = document.getElementById('type-modal-title');
const typeForm = document.getElementById('type-form');

const stateModal = document.getElementById('state-modal');
const stateModalTitle = document.getElementById('state-modal-title');
const stateForm = document.getElementById('state-form');

const companyBrandModal = document.getElementById('company-brand-modal');
const companyBrandForm = document.getElementById('company-brand-form');
const associateBrandIdSelect = document.getElementById('associate-brand-id');

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
    }, 5000); // Hide after 5 seconds
}

function showPage(pageId) {
    pages.forEach(page => page.classList.add('hidden'));

    const targetPage = document.getElementById(pageId);
    if (targetPage) {
        targetPage.classList.remove('hidden');
    } else {
        console.error(`Error de UI: No se encontró el elemento con ID "${pageId}". Por favor, verifica tu index.html.`);
        showError(`Error al cargar la sección de la página. (ID: ${pageId})`);
        return;
    }

    navButtons.forEach(btn => btn.classList.remove('active'));
    // CORREGIDO: Asegurarse de que 'nav-auth' sea el ID correcto para la página de autenticación
    const navBtnId = `nav-${pageId.replace('-page', '') === 'auth' ? 'auth' : pageId.replace('-page', '')}`; // 'auth-page' -> 'nav-auth'
    const navBtnToActivate = document.getElementById(navBtnId);
    if (navBtnToActivate) {
        navBtnToActivate.classList.add('active');
    } else {
        console.warn(`Advertencia de UI: No se encontró el botón de navegación para la página "${pageId}". ID esperado: ${navBtnId}`);
    }
}

function updateNavButtons() {
    const isLoggedIn = CURRENT_USER !== null;
    const isClient = USER_ROLE === 'cliente';
    const isProvider = USER_ROLE === 'proveedor';

    navLoginBtn.classList.toggle('hidden', isLoggedIn); // Ahora navLoginBtn es nav-auth
    navLogoutBtn.classList.toggle('hidden', !isLoggedIn);
    navClientDashboardBtn.classList.toggle('hidden', !isLoggedIn || !isClient);
    navProviderDashboardBtn.classList.toggle('hidden', !isLoggedIn || !isProvider);
}

function isAuthenticated() {
    return USER_TOKEN !== null;
}

function isClient() {
    return isAuthenticated() && USER_ROLE === 'cliente';
}

function isProvider() {
    return isAuthenticated() && USER_ROLE === 'proveedor';
}

async function fetchApi(endpoint, method = 'GET', body = null, requiresAuth = false) {
    showLoader();
    errorMessageDiv.classList.add('hidden'); // Hide previous errors

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
        throw error; // Re-throw to be caught by specific handler
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
             localStorage.removeItem('userCompanyId'); // Clear if not provider or no company
        }

        CURRENT_USER = data.userId;
        USER_TOKEN = data.token;
        USER_ROLE = data.userRole;
        USER_COMPANY_ID = data.companyId || null;

        alert('Inicio de sesión exitoso!');
        loginForm.reset();
        updateAuthUI();
        showPage('catalog-page');
        loadCatalog();
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
        registerData.companyName = document.getElementById('company-name').value;
        registerData.companyEmail = document.getElementById('company-email').value;
        registerData.companyPhone = document.getElementById('company-phone').value;
        registerData.companyAddress = document.getElementById('company-address').value;
        registerData.companyWebSite = document.getElementById('company-website').value;
    }

    try {
        const data = await fetchApi('/register', 'POST', registerData);
        localStorage.setItem('userToken', data.token);
        localStorage.setItem('currentUserId', data.userId);
        localStorage.setItem('userRole', data.userRole);
        if (data.companyId) {
            localStorage.setItem('userCompanyId', data.companyId);
        } else {
            localStorage.removeItem('userCompanyId'); // Clear if not provider or no company
        }

        CURRENT_USER = data.userId;
        USER_TOKEN = data.token;
        USER_ROLE = data.userRole;
        USER_COMPANY_ID = data.companyId || null;

        alert('Registro exitoso!');
        registerForm.reset();
        providerFieldsDiv.classList.add('hidden'); // Hide provider fields
        updateAuthUI();
        showPage('catalog-page');
        loadCatalog();
    }
    catch (error) {
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
    loadCatalog(); // Refresh catalog
}

function updateAuthUI() {
    updateNavButtons();
    // Reset forms and hide/show relevant sections
    loginForm.reset();
    registerForm.reset();
    registerUserTypeSelect.value = '';
    providerFieldsDiv.classList.add('hidden');

    // Hide/show review form based on client role
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
    } catch (error) {
        // Error already displayed
    }
}

function renderEquipmentList(equipmentList, container, context = 'catalog') {
    container.innerHTML = ''; // Clear previous content
    if (equipmentList.length === 0) {
        container.innerHTML = '<p>No hay equipos disponibles en este momento.</p>';
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
            ${context === 'catalog' ? '<button class="btn view-detail-btn" data-id="' + equipment.id + '">Ver Detalles</button>' : ''}
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

        // Add event listener for "Ver Detalles" button
        if (context === 'catalog') {
            card.querySelector('.view-detail-btn').addEventListener('click', (e) => {
                const equipmentId = e.target.dataset.id;
                loadEquipmentDetail(equipmentId);
            });
        }
        // Add event listeners for provider/client dashboard buttons
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
        showPage('catalog-page'); // Go back to catalog if error
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
            <p><strong>Proveedor:</strong> ${equipment.providerName} (${equipment.companyName || 'N/A'})</p>
            <p>Publicado el: ${new Date(equipment.createdAt).toLocaleDateString()}</p>
            ${isClient() ? `<button id="add-to-favorites-btn" class="btn" data-id="${equipment.id}">Añadir a Favoritos</button>` : ''}
        </div>
    `;

    // Add event listener for Add to Favorites button
    if (isClient()) {
        document.getElementById('add-to-favorites-btn').addEventListener('click', (e) => {
            addToFavorites(e.target.dataset.id);
        });
    }

    // Prepare review form
    reviewStarValueInput.value = '';
    reviewBodyInput.value = '';
    submitReviewBtn.dataset.equipmentId = equipment.id;
    updateAuthUI(); // Ensure review form visibility is correct based on role
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
    const equipmentId = submitReviewBtn.dataset.equipmentId;
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
        loadReviewsForEquipment(equipmentId); // Reload reviews
        if (USER_ROLE === 'cliente') loadMyReviews(); // Refresh client dashboard reviews
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
        // Refresh appropriate lists
        const currentEquipmentId = submitReviewBtn.dataset.equipmentId; // If on detail page
        if (currentEquipmentId) loadReviewsForEquipment(currentEquipmentId);
        loadMyReviews(); // Always refresh own reviews
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
        await fetchApi('/favorites', 'POST', { equipmentId }, true);
        alert('Equipo añadido a favoritos!');
        if (USER_ROLE === 'cliente') loadMyFavorites(); // Refresh client dashboard favorites
    } catch (error) {
        // Error already displayed
    }
}

async function removeFavorite(favoriteId) {
    if (!isClient()) return; // Should not happen due to UI, but for safety
    if (!confirm('¿Estás seguro de que quieres eliminar este favorito?')) return;
    try {
        await fetchApi(`/favorites/${favoriteId}`, 'DELETE', null, true);
        alert('Favorito eliminado con éxito!');
        loadMyFavorites(); // Refresh favorites list
    } catch (error) {
        // Error already displayed
    }
}

async function loadMyFavorites() {
    if (!isClient()) return;
    try {
        const favorites = await fetchApi(`/users/${CURRENT_USER}/favorites`, 'GET', null, true);
        // Favorites DTO only returns favorite ID, user ID, equipment ID. We need equipment details.
        // This is inefficient but simple for vanilla JS. In a real app, backend would return joined data.
        const equipmentPromises = favorites.map(f =>
            fetchApi(`/equipment/${f.idEquipment}`).then(eq => ({ ...eq, favoriteId: f.id }))
        );
        const equipmentDetails = await Promise.all(equipmentPromises);
        renderEquipmentList(equipmentDetails, myFavoritesListDiv, 'client-favorites');
    } catch (error) {
        myFavoritesListDiv.innerHTML = '<p>Error al cargar mis favoritos.</p>';
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
    // Las marcas, tipos y estados ya no se gestionan desde esta pestaña
    // Los datos se cargarán en los selectores del modal de equipo.
}

async function loadMyEquipment() {
    if (!isProvider()) return;
    try {
        // Frontend va a obtener el ID del proveedor a partir del ID del usuario actual
        const providerData = await fetchApi(`/users/${CURRENT_USER}/provider`, 'GET', null, true);
        if (!providerData || !providerData.id) {
            myEquipmentListDiv.innerHTML = '<p>No se pudo cargar la información del proveedor. Asegúrate de que el backend tenga el endpoint /api/users/{id}/provider y que devuelva el ID del proveedor.</p>';
            return;
        }
        const providerId = providerData.id;

        // Ahora, se solicita al backend los equipos de ESE proveedor.
        const myEquipment = await fetchApi(`/equipment/provider/${providerId}`, 'GET', null, true);
        renderEquipmentList(myEquipment, myEquipmentListDiv, 'provider-dashboard');
    } catch (error) {
        myEquipmentListDiv.innerHTML = '<p>Error al cargar mis equipos.</p>';
    }
}


async function openEquipmentModalForEdit(equipmentId = null) {
    // Populate dropdowns with current data
    await populateEquipmentFormDropdowns();
    equipmentModal.classList.remove('hidden');

    if (equipmentId) {
        equipmentModalTitle.textContent = 'Editar Equipo';
        try {
            const equipment = await fetchApi(`/equipment/${equipmentId}`);
            document.getElementById('equipment-id').value = equipment.id;
            document.getElementById('equipment-name').value = equipment.name;
            document.getElementById('equipment-description').value = equipment.description;
            document.getElementById('equipment-price').value = equipment.price;
            // CORREGIDO: Usar la referencia correcta al input de URL de imagen
            equipmentUrlImageInput.value = equipment.urlImage;
            document.getElementById('equipment-type-id').value = equipment.idTypeEquipment;
            document.getElementById('equipment-state-id').value = equipment.idStateEquipment;
            document.getElementById('equipment-brand-id').value = equipment.idBrand;
        } catch (error) {
            alert('Error al cargar datos del equipo para edición.');
            equipmentModal.classList.add('hidden');
        }
    } else {
        equipmentModalTitle.textContent = 'Añadir Equipo';
        equipmentForm.reset();
        document.getElementById('equipment-id').value = ''; // Clear ID for new equipment
    }
}

async function populateEquipmentFormDropdowns() {
    try {
        const types = await fetchApi('/types-equipment');
        populateSelect(equipmentTypeIdSelect, types, 'id', 'type');
        const states = await fetchApi('/states-equipment');
        populateSelect(equipmentStateIdSelect, states, 'id', 'state');
        const brands = await fetchApi('/brands');
        populateSelect(equipmentBrandIdSelect, brands, 'id', 'nameBrand');
    } catch (error) {
        showError('No se pudieron cargar las opciones para el equipo.');
        equipmentModal.classList.add('hidden');
    }
}

function populateSelect(selectElement, data, valueKey, textKey) {
    // Mejorado el placeholder, si el elemento previo es input y tiene placeholder, úsalo
    // o un valor por defecto basado en el ID del select
    const placeholderText = selectElement.previousElementSibling && selectElement.previousElementSibling.placeholder
                            ? selectElement.previousElementSibling.placeholder
                            : selectElement.id.replace(/-/g, ' '); // Reemplazar guiones por espacios
    selectElement.innerHTML = `<option value="">Selecciona ${placeholderText}</option>`;

    data.forEach(item => {
        const option = document.createElement('option');
        option.value = item[valueKey];
        option.textContent = item[textKey];
        selectElement.appendChild(option);
    });
}

async function handleEquipmentFormSubmit(event) {
    event.preventDefault();
    const equipmentId = document.getElementById('equipment-id').value;
    const method = equipmentId ? 'PUT' : 'POST';
    const url = equipmentId ? `/equipment/${equipmentId}` : '/equipment';

    const equipmentData = {
        name: document.getElementById('equipment-name').value,
        description: document.getElementById('equipment-description').value,
        price: parseFloat(document.getElementById('equipment-price').value),
        // CORREGIDO: Obtener el valor del input de URL de imagen
        urlImage: equipmentUrlImageInput.value, 
        typeId: parseInt(document.getElementById('equipment-type-id').value),
        stateId: parseInt(document.getElementById('equipment-state-id').value),
        brandId: parseInt(document.getElementById('equipment-brand-id').value),
    };

    try {
        await fetchApi(url, method, equipmentData, true);
        alert(equipmentId ? 'Equipo actualizado con éxito!' : 'Equipo añadido con éxito!');
        equipmentModal.classList.add('hidden');
        loadMyEquipment(); // Refresh provider's equipment list
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
        loadMyEquipment(); // Refresh provider's equipment list
        loadCatalog(); // Refresh main catalog
    } catch (error) { // AGREGADO: Bloque catch para manejar errores
        console.error("Error al eliminar equipo:", error);
    }
}

async function loadCompanyDetails() {
    if (!isProvider() || !USER_COMPANY_ID) {
        companyDetailsView.innerHTML = '<p>No hay información de compañía asociada o no tienes permisos.</p>';
        return;
    }
    try {
        const company = await fetchApi(`/companies/${USER_COMPANY_ID}`);
        document.getElementById('view-company-name').textContent = company.name;
        document.getElementById('view-company-email').textContent = company.email || 'N/A';
        document.getElementById('view-company-phone').textContent = company.phone || 'N/A';
        document.getElementById('view-company-address').textContent = company.address || 'N/A';
        document.getElementById('view-company-website').textContent = company.webSite || 'N/A';
        
        // Populate edit form fields
        document.getElementById('edit-company-name').value = company.name;
        document.getElementById('edit-company-email').value = company.email;
        document.getElementById('edit-company-phone').value = company.phone;
        document.getElementById('edit-company-address').value = company.address;
        document.getElementById('edit-company-website').value = company.webSite;

        loadCompanyBrands(USER_COMPANY_ID);

    } catch (error) {
        companyDetailsView.innerHTML = '<p>Error al cargar la información de la compañía.</p>';
    }
}

async function handleEditCompanyFormSubmit(event) {
    event.preventDefault();
    if (!USER_COMPANY_ID) return;

    const companyData = {
        id: USER_COMPANY_ID, // Include ID for update
        name: document.getElementById('edit-company-name').value,
        email: document.getElementById('edit-company-email').value,
        phone: document.getElementById('edit-company-phone').value,
        address: document.getElementById('edit-company-address').value,
        webSite: document.getElementById('edit-company-website').value,
    };

    try {
        await fetchApi(`/companies/${USER_COMPANY_ID}`, 'PUT', companyData, true);
        alert('Información de compañía actualizada con éxito!');
        companyDetailsView.classList.remove('hidden');
        editCompanyForm.classList.add('hidden');
        loadCompanyDetails(); // Reload company details
    } catch (error) {
        // Error already displayed
    }
}

// Company Brands Management
async function loadCompanyBrands(companyId) {
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
            li.innerHTML = `
                <div class="list-item-content">
                    <span>${brand.nameBrand}</span>
                </div>
                <div class="list-item-buttons">
                    <button class="btn delete-btn" data-brand-id="${brand.id}" data-company-id="${companyId}">Desasociar</button>
                </div>
            `;
            li.querySelector('.delete-btn').addEventListener('click', (e) => removeCompanyBrand(e.target.dataset.companyId, e.target.dataset.brandId));
            companyBrandsListDiv.appendChild(li);
        });
    } catch (error) {
        companyBrandsListDiv.innerHTML = '<p>Error al cargar marcas asociadas.</p>';
    }
}

async function openAssociateBrandModal() {
    try {
        const allBrands = await fetchApi('/brands'); // Get all available brands
        populateSelect(associateBrandIdSelect, allBrands, 'id', 'nameBrand');
        companyBrandModal.classList.remove('hidden');
    } catch (error) {
        showError('No se pudieron cargar las marcas para asociar.');
    }
}

async function handleCompanyBrandFormSubmit(event) {
    event.preventDefault();
    if (!USER_COMPANY_ID) return;
    const brandId = associateBrandIdSelect.value;

    if (!brandId) {
        showError('Por favor, selecciona una marca.');
        return;
    }

    try {
        await fetchApi(`/companies/${USER_COMPANY_ID}/brands/${brandId}`, 'POST', null, true);
        alert('Marca asociada con éxito!');
        companyBrandModal.classList.add('hidden');
        loadCompanyBrands(USER_COMPANY_ID);
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

// MEJORADO: renderGenericList para NO mostrar botones de editar/eliminar en Types y States
function renderGenericList(items, container, entityType, nameKey) {
    container.innerHTML = ''; // Limpiar el contenido previo
    if (items.length === 0) {
        container.innerHTML = `<p>No hay ${entityType}s disponibles.</p>`;
        return;
    }
    items.forEach(item => {
        const li = document.createElement('div');
        li.className = 'list-item';
        li.innerHTML = `
            <div class="list-item-content">
                <span>${item[nameKey]}</span>
            </div>
            <div class="list-item-buttons">
                ${entityType === 'brand' ? `
                    <button class="btn edit-btn" data-id="${item.id}" data-type="${entityType}">Editar</button>
                    <button class="btn delete-btn" data-id="${item.id}" data-type="${entityType}">Eliminar</button>
                ` : ''}
                <!-- Botón "Ver Detalles" para Tipos y Estados, ya que no son editables/eliminables -->
                ${entityType === 'type' || entityType === 'state' ? `
                    <button class="btn" data-id="${item.id}" data-type="${entityType}">Ver Detalles</button>
                ` : ''}
            </div>
        `;
        // Adjuntar event listeners solo si los botones son renderizados para este entityType
        if (entityType === 'brand') {
            li.querySelector('.edit-btn').addEventListener('click', (e) => openGenericModalForEdit(e.target.dataset.id, e.target.dataset.type));
            li.querySelector('.delete-btn').addEventListener('click', (e) => deleteGenericEntity(e.target.dataset.id, e.target.dataset.type));
        } else if (entityType === 'type' || entityType === 'state') {
            // Adjuntar listener para "Ver Detalles" en Tipos y Estados
            li.querySelector('.btn').addEventListener('click', (e) => openGenericModalForEdit(e.target.dataset.id, e.target.dataset.type));
        }
        container.appendChild(li);
    });
}

// MEJORADO: openGenericModalForEdit para deshabilitar inputs y ocultar botones para tipos/estados
async function openGenericModalForEdit(id = null, entityType) {
    const modal = document.getElementById(`${entityType}-modal`);
    const form = document.getElementById(`${entityType}-form`);
    const title = document.getElementById(`${entityType}-modal-title`);
    const idInput = form.querySelector(`#${entityType}-id`);
    const nameInput = form.querySelector(`#${entityType}-name`);
    const submitBtn = form.querySelector('button[type="submit"]');

    if (entityType === 'type' || entityType === 'state') {
        nameInput.disabled = true; // Deshabilitar input
        if (submitBtn) submitBtn.classList.add('hidden'); // Ocultar botón de guardar
        title.textContent = `Detalles de ${entityType.charAt(0).toUpperCase() + entityType.slice(1)}`; // Título de "Detalles"
    } else { // Es una marca
        nameInput.disabled = false;
        if (submitBtn) submitBtn.classList.remove('hidden'); // Mostrar botón de guardar
        title.textContent = id ? `Editar ${entityType.charAt(0).toUpperCase() + entityType.slice(1)}` : `Añadir ${entityType.charAt(0).toUpperCase() + entityType.slice(1)}`; // Título de "Editar" o "Añadir"
    }

    modal.classList.remove('hidden');

    if (id) {
        try {
            // Los endpoints correctos son /types-equipment y /states-equipment
            const data = await fetchApi(`/${entityType}s${entityType === 'type' || entityType === 'state' ? '-equipment' : ''}/${id}`);
            idInput.value = data.id;
            // CORREGIDO: nameKey para la propiedad del objeto de respuesta (type, state, nameBrand)
            nameInput.value = data[entityType === 'brand' ? 'nameBrand' : entityType];
        } catch (error) {
            alert(`Error al cargar datos de ${entityType} para ${entityType === 'brand' ? 'edición' : 'visualización'}.`);
            modal.classList.add('hidden');
        }
    } else {
        // Si se intenta "añadir" y es tipo/estado, mostrar error y ocultar modal
        if (entityType === 'type' || entityType === 'state') {
             showError(`No se permite añadir ${entityType}s directamente desde la UI. Estos valores son fijos.`);
             modal.classList.add('hidden');
             return;
        }
        form.reset();
        idInput.value = '';
    }
}

// MEJORADO: handleGenericFormSubmit para bloquear envío de tipos/estados
async function handleGenericFormSubmit(event) {
    event.preventDefault();
    const form = event.target;
    const entityType = form.id.replace('-form', '');
    const id = form.querySelector(`#${entityType}-id`).value;
    const name = form.querySelector(`#${entityType}-name`).value;

    // Bloquear submit para tipos y estados desde el frontend
    if (entityType === 'type' || entityType === 'state') {
        showError(`No se permite modificar ${entityType}s directamente desde la UI.`);
        document.getElementById(`${entityType}-modal`).classList.add('hidden');
        return;
    }

    const method = id ? 'PUT' : 'POST';
    const url = id ? `/${entityType}s/${id}` : `/${entityType}s`;

    const body = {};
    if (entityType === 'brand') body.nameBrand = name;
    // Ya no hay manejo de `type` o `state` aquí porque no se pueden modificar

    try {
        await fetchApi(url, method, body, true); // Requires auth
        alert(`${entityType.charAt(0).toUpperCase() + entityType.slice(1)} guardado con éxito!`);
        document.getElementById(`${entityType}-modal`).classList.add('hidden');
        // Refresh appropriate list
        if (entityType === 'brand') loadBrands();
        // Las listas de tipos y estados no necesitan ser refrescadas ya que no se modifican
        if (equipmentModal.classList.contains('hidden') === false) { // Si el modal de equipo está abierto
             populateEquipmentFormDropdowns();
        }

    } catch (error) {
        // Error already displayed
    }
}

// MEJORADO: deleteGenericEntity para bloquear eliminación de tipos/estados
async function deleteGenericEntity(id, entityType) {
    // Bloquear eliminación para tipos y estados desde el frontend
    if (entityType === 'type' || entityType === 'state') {
        showError(`No se permite eliminar ${entityType}s directamente desde la UI.`);
        return;
    }

    if (!confirm(`¿Estás seguro de que quieres eliminar este ${entityType}? Esta acción es irreversible.`)) return;
    try {
        await fetchApi(`/${entityType}s/${id}`, 'DELETE', null, true); // Requires auth
        alert(`${entityType.charAt(0).toUpperCase() + entityType.slice(1)} eliminado con éxito!`);
        // Refresh appropriate list
        if (entityType === 'brand') loadBrands();
        // Las listas de tipos y estados no necesitan ser refrescadas ya que no se modifican
        if (equipmentModal.classList.contains('hidden') === false) {
             populateEquipmentFormDropdowns();
        }
    } catch (error) {
        console.error(`Error al eliminar ${entityType}:`, error);
    }
}

// --- Event Listeners ---

// Navigation
navCatalogBtn.addEventListener('click', () => { showPage('catalog-page'); loadCatalog(); });
navLoginBtn.addEventListener('click', () => showPage('auth-page')); // navLoginBtn es ahora nav-auth
navLogoutBtn.addEventListener('click', handleLogout);
navProviderDashboardBtn.addEventListener('click', loadProviderDashboard);
navClientDashboardBtn.addEventListener('click', () => { showPage('client-dashboard-page'); loadMyFavorites(); });
backToCatalogBtn.addEventListener('click', () => { showPage('catalog-page'); loadCatalog(); });

// Auth Forms
loginForm.addEventListener('submit', handleLogin);
registerForm.addEventListener('submit', handleRegister);
registerUserTypeSelect.addEventListener('change', (e) => {
    providerFieldsDiv.classList.toggle('hidden', e.target.value !== 'proveedor');
});

// Review Submit
submitReviewBtn.addEventListener('click', submitReview);

// Provider Dashboard
providerTabs.forEach(tab => {
    tab.addEventListener('click', (e) => {
        document.querySelectorAll('#provider-dashboard-page .tab-content').forEach(content => content.classList.add('hidden'));
        document.querySelectorAll('#provider-dashboard-page .tab-button').forEach(btn => btn.classList.remove('active'));
        document.getElementById(`${e.target.dataset.tab}-tab`).classList.remove('hidden');
        e.target.classList.add('active');

        // Load data specific to the tab
        if (e.target.dataset.tab === 'provider-equipment') loadMyEquipment();
        else if (e.target.dataset.tab === 'provider-company') loadCompanyDetails();
    });
});

addEquipmentBtn.addEventListener('click', () => openEquipmentModalForEdit(null));
equipmentForm.addEventListener('submit', handleEquipmentFormSubmit);

editCompanyBtn.addEventListener('click', () => {
    companyDetailsView.classList.add('hidden');
    editCompanyForm.classList.remove('hidden');
});
cancelEditCompanyBtn.addEventListener('click', () => {
    companyDetailsView.classList.remove('hidden');
    editCompanyForm.classList.add('hidden');
});
editCompanyForm.addEventListener('submit', handleEditCompanyFormSubmit);

addCompanyBrandBtn.addEventListener('click', openAssociateBrandModal);
companyBrandForm.addEventListener('submit', handleCompanyBrandFormSubmit);


// Client Dashboard
clientTabs.forEach(tab => {
    tab.addEventListener('click', (e) => {
        document.querySelectorAll('#client-dashboard-page .tab-content').forEach(content => content.classList.add('hidden'));
        document.querySelectorAll('#client-dashboard-page .tab-button').forEach(btn => btn.classList.remove('active'));
        document.getElementById(`${e.target.dataset.tab}-tab`).classList.remove('hidden');
        e.target.classList.add('active');

        // Load data specific to the tab
        if (e.target.dataset.tab === 'client-favorites') loadMyFavorites();
        else if (e.target.dataset.tab === 'client-reviews') loadMyReviews();
    });
});

// Generic Add Buttons (Solo Marca es editable/añadible)
addBrandBtn.addEventListener('click', () => openGenericModalForEdit(null, 'brand'));
brandForm.addEventListener('submit', handleGenericFormSubmit);


// --- Initialization ---
function init() {
    // Check for existing token in localStorage
    USER_TOKEN = localStorage.getItem('userToken');
    CURRENT_USER = localStorage.getItem('currentUserId');
    USER_ROLE = localStorage.getItem('userRole');
    USER_COMPANY_ID = localStorage.getItem('userCompanyId');

    updateAuthUI();
    showPage('catalog-page');
    loadCatalog();
}

document.addEventListener('DOMContentLoaded', init);