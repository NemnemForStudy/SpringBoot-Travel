document.addEventListener("DOMContentLoaded", () => {
    fetch("/board/travelDestination")
        .then(response => response.json())
        .then(data => {
        console.log("data : " + data);
            const list = document.getElementById("board-list");
            data.forEach(board => {
                const li = document.createElement("li");
                li.innerHTML = `<strong>${board.title} - ${board.content}`;
                list.appendChild(li);
            });
        })
        .catch(error => console.error("Error fetching boards : ", error));
});