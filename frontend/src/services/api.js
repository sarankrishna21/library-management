const API_BASE_URL = "http://localhost:8080";

/**
 * Helper function for handling fetch responses safely.
 */
async function handleResponse(response) {
  const isJson = response.headers.get("content-type")?.includes("application/json");
  const data = isJson ? await response.json() : null;

  if (!response.ok) {
    const error = (data && data.error) || response.statusText || "An unexpected error occurred.";
    throw new Error(error);
  }
  return data;
}

// ==========================================
// BOOKS
// ==========================================
export const bookApi = {
  getAll: () => fetch(`${API_BASE_URL}/api/books`).then(handleResponse),
  
  getById: (id) => fetch(`${API_BASE_URL}/api/books/${id}`).then(handleResponse),
  
  create: (bookData) => fetch(`${API_BASE_URL}/api/books`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(bookData)
  }).then(handleResponse),
  
  update: (id, bookData) => fetch(`${API_BASE_URL}/api/books/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(bookData)
  }).then(handleResponse),
  
  delete: (id) => fetch(`${API_BASE_URL}/api/books/${id}`, {
    method: "DELETE"
  }).then(handleResponse),
  
  searchTitle: (title) => fetch(`${API_BASE_URL}/api/books/search/title/${encodeURIComponent(title)}`).then(handleResponse),
  searchAuthor: (author) => fetch(`${API_BASE_URL}/api/books/search/author/${encodeURIComponent(author)}`).then(handleResponse),
  searchCategory: (category) => fetch(`${API_BASE_URL}/api/books/search/category/${encodeURIComponent(category)}`).then(handleResponse)
};

// ==========================================
// USERS
// ==========================================
export const userApi = {
  getAll: () => fetch(`${API_BASE_URL}/api/users`).then(handleResponse),
  
  getById: (id) => fetch(`${API_BASE_URL}/api/users/${id}`).then(handleResponse),
  
  create: (userData) => fetch(`${API_BASE_URL}/api/users`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(userData)
  }).then(handleResponse),
  
  update: (id, userData) => fetch(`${API_BASE_URL}/api/users/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(userData)
  }).then(handleResponse),
  
  delete: (id) => fetch(`${API_BASE_URL}/api/users/${id}`, {
    method: "DELETE"
  }).then(handleResponse)
};

// ==========================================
// LIBRARY
// ==========================================
export const libraryApi = {
  borrowBook: (transactionId, userId, bookId) => fetch(`${API_BASE_URL}/api/library/borrow`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ transactionId, userId, bookId })
  }).then(handleResponse),

  returnBook: (transactionId) => fetch(`${API_BASE_URL}/api/library/return`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ transactionId })
  }).then(handleResponse),

  getTransactions: () => fetch(`${API_BASE_URL}/api/library/transactions`).then(handleResponse),
  
  getTransaction: (id) => fetch(`${API_BASE_URL}/api/library/transactions/${id}`).then(handleResponse)
};
