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
    
    // fetch로 서버에서 Board 정보 가져오기
    fetch(`/board/api/board/${boardId}`)
        .then(res => res.json())
        .then(board => {
            if (!board.coordinates) return; // coordinates가 없으면 종료
            console.log(board.coordinates);
            
            board.coordinates.forEach(coord => {
                var map = new naver.maps.Map('map', {
                    center: new naver.maps.LatLng(coord.latitude, coord.longitude),
                    zoom: 15
                });

                var marker = new naver.maps.Marker({
                    position: new naver.maps.LatLng(coord.latitude, coord.longitude),
                    map: map,
                    title: "여행지 위치",
                    clickable: true,
                });

                // naverMap은 addListener를 사용해야함.
                naver.maps.Event.addListener(marker, "click", function() {
                    alert("마커 클릭");
                });
                
                marker.getElement().style.cursor = "pointer";
            });
        })
        .catch(err => console.error(err));
});

