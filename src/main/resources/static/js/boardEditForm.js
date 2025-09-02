document.addEventListener("DOMContentLoaded", () => {
    // 변수 선언 (충돌 피하기 위해 이름 변경)
    const menuButton = document.getElementById('menuBtn');
    const dropdownMenuElement = document.getElementById('dropdownMenu');
    const boardId = document.body.getAttribute("data-board-id");

    // 메뉴 버튼 클릭 시 드롭다운 토글
    menuButton.addEventListener('click', (e) => {
        e.stopPropagation(); // 클릭 이벤트가 document로 전파되는 걸 막음
        dropdownMenuElement.style.display =
            dropdownMenuElement.style.display === 'block' ? 'none' : 'block';
    });

    // 드롭다운 밖 클릭 시 닫기
    document.addEventListener('click', (e) => {
        if (!menuButton.contains(e.target) && !dropdownMenuElement.contains(e.target)) {
            dropdownMenuElement.style.display = 'none';
        }
    });

    // 수정 버튼 클릭
    document.getElementById('editBtn').addEventListener('click', () => {
        debugger;
        const boardId = document.body.getAttribute('data-board-id');
        window.location.href = `/board/update?boardId=${boardId}`;
    });
    
    const map = new naver.maps.Map('map', {
        center: new naver.maps.LatLng(37.5665, 126.9780),
        zoom: 13
    });
});

