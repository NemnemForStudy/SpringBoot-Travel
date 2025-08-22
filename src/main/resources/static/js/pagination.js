function renderPagination(totalPosts, postsPerPage) {
    const totalPages = Math.ceil(totalPosts / postsPerPage);
    const pagination = document.querySelector(".pagination");

    pagination.innerHTML = "";

    const prev = document.createElement("li");
    prev.className = "page-item disabled"
    prev.innerHTML = `<a class="page-link">Previous</a>`;
    pagination.appendChild(prev);
    
    for(let i = 1; i <= totalPages; i++) {
        const li = document.createElement("li");
        li.className = "page-item";
        li.innerHTML = `<a class="page-link" href="#">${i}</a>`;
        pagination.appendChild(li);
    }

    // Next
    const next = document.createElement("li");
    next.className = "page-item";
    next.innerHTML = `<a class="page-link"href="#">Next</a>`;
    pagination.appendChild(next);
}

document.querySelector(".pagination").addEventListener("click", (e) => {
    if(e.target.classList.contains("page-link")) {
        const page = e.target.textContent.trim();
        if(page === "Previous") {
            console.log("이전 페이지 이동");
        } else if(page === "Next") {
            console.log("다음 페이지 이동");
        } else {
            console.log(`${page} 페이지 이동`);
        }
    }
})