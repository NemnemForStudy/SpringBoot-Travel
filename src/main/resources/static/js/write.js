document.getElementById('saveBtn').addEventListener('click', function(event) {
    event.preventDefault();  // 폼 제출을 막고, JavaScript로 처리하도록 함
    const title = document.getElementById('title').value;
    const content = document.getElementById('content').value;

    // 디버깅을 위한 로그 출력
    console.log("제목:", title);
    console.log("내용:", content);

    fetch('/board/write', { // 저장 경로
        method: 'POST',
        headers: {
            'Content-Type': 'application/json' // Content-Type은 json 방식으로 설정
        },
        body: JSON.stringify({
            title: title,
            content: content
        })
    })
    .then(response => {
        if (!response.ok) throw new Error('서버 오류 발생');
        return response.json();
    })
    .then(data => {
        console.log("서버 응답:", data);  // 서버 응답 로그
        if (data.success) {
            alert('글이 성공적으로 등록되었습니다!');
            window.location.href = '/travelDestination';
        } else {
            alert('글 등록 실패: ' + data.message);
        }
    })
    .catch(error => {
        alert('에러 발생 : ' + error.message);
    });
});
