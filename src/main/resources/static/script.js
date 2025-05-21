const API_BASE_URL = 'http://localhost:8080/posts';
const USER_API_URL = 'http://localhost:8080/users';

const postsList = document.getElementById('postsList');
const addPostForm = document.getElementById('addPostForm');
const editPostForm = document.getElementById('editPostForm');
const searchByUserBtn = document.getElementById('searchByUserBtn');
const usernameSearch = document.getElementById('usernameSearch');
const editPostModal = new bootstrap.Modal(document.getElementById('editPostModal'));

document.addEventListener('DOMContentLoaded', loadAllPosts);

addPostForm.addEventListener('submit', function(e) {
    e.preventDefault();

    const username = document.getElementById('username').value;
    const title = document.getElementById('postTitle').value;
    const text = document.getElementById('postText').value;

    fetch(`${USER_API_URL}/username/${username}`)
        .then(response => {
            if (!response.ok) throw new Error('Пользователь не найден');
            return response.json();
        })
        .then(user => {
            const postData = {
                title,
                text,
                publishingDate: new Date().toISOString()
            };

            return fetch(`${API_BASE_URL}/create?userId=${user.id}`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(postData)
            });
        })
        .then(response => {
            if (!response.ok) throw new Error('Ошибка при создании поста');
            return response.json();
        })
        .then(() => {
            alert('Пост успешно создан!');
            addPostForm.reset();
            loadAllPosts();
        })
        .catch(error => {
            alert('Ошибка: ' + error.message);
        });
});

searchByUserBtn.addEventListener('click', () => {
    const username = usernameSearch.value.trim();
    if (username) {
        fetch(`${API_BASE_URL}/byuser/${username}`)
            .then(response => {
                if (!response.ok) throw new Error('Ошибка при поиске постов');
                return response.json();
            })
            .then(posts => {
                renderPosts(posts);
                document.querySelector('#postsContainer h2').textContent = `Посты пользователя ${username}`;
            })
            .catch(error => alert('Ошибка: ' + error.message));
    } else {
        loadAllPosts();
    }
});

function loadAllPosts() {
    fetch(API_BASE_URL)
        .then(response => {
            if (!response.ok) throw new Error('Ошибка при загрузке постов');
            return response.json();
        })
        .then(posts => {
            renderPosts(posts);
            document.querySelector('#postsContainer h2').textContent = 'Все посты';
        })
        .catch(error => alert('Ошибка: ' + error.message));
}

function renderPosts(posts) {
    postsList.innerHTML = '';

    if (posts.length === 0) {
        postsList.innerHTML = '<div class="col-12"><div class="alert alert-info">Посты не найдены</div></div>';
        return;
    }

    posts.forEach(post => {
        const postCard = document.createElement('div');
        postCard.className = 'col-md-6 col-lg-4';
        postCard.innerHTML = `
            <div class="card post-card h-100">
                <div class="card-body">
                    <h5 class="card-title">${post.title}</h5>
                    <p class="card-text">${post.text}</p>
                    <p class="text-muted small">Опубликовано: ${new Date(post.publishingDate).toLocaleString()}</p>
                    <p class="text-muted small">Автор: ${post.user ? post.user.username : 'Неизвестен'}</p>
                </div>
                <div class="card-footer action-buttons bg-transparent border-0">
                    <button class="btn btn-sm btn-outline-primary edit-post" data-id="${post.id}">Редактировать</button>
                    <button class="btn btn-sm btn-outline-danger delete-post" data-id="${post.id}">Удалить</button>
                </div>
            </div>
        `;
        postsList.appendChild(postCard);
    });

    document.querySelectorAll('.edit-post').forEach(btn => {
        btn.addEventListener('click', () => openEditModal(btn.dataset.id));
    });

    document.querySelectorAll('.delete-post').forEach(btn => {
        btn.addEventListener('click', () => {
            if (confirm('Удалить пост?')) deletePost(btn.dataset.id);
        });
    });
}

function openEditModal(postId) {
    fetch(`${API_BASE_URL}/${postId}`)
        .then(response => {
            if (!response.ok) throw new Error('Ошибка при загрузке поста');
            return response.json();
        })
        .then(post => {
            document.getElementById('editPostId').value = post.id;
            document.getElementById('editPostTitle').value = post.title;
            document.getElementById('editPostText').value = post.text;
            editPostModal.show();
        })
        .catch(error => alert('Ошибка: ' + error.message));
}

document.getElementById('savePostChanges').addEventListener('click', () => {
    const postId = document.getElementById('editPostId').value;
    const title = document.getElementById('editPostTitle').value;
    const text = document.getElementById('editPostText').value;

    fetch(`${API_BASE_URL}/${postId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ title, text })
    })
        .then(response => {
            if (!response.ok) throw new Error('Ошибка при обновлении поста');
            return response.json();
        })
        .then(() => {
            editPostModal.hide();
            alert('Пост обновлён!');
            loadAllPosts();
        })
        .catch(error => alert('Ошибка: ' + error.message));
});

function deletePost(postId) {
    fetch(`${API_BASE_URL}/${postId}`, { method: 'DELETE' })
        .then(response => {
            if (!response.ok) throw new Error('Ошибка при удалении');
            alert('Пост удалён!');
            loadAllPosts();
        })
        .catch(error => alert('Ошибка: ' + error.message));
}
