document.getElementById('saveBtn').addEventListener('click', async function(event) {
    event.preventDefault();  // 폼 제출 막기
    const title = document.getElementById('title').value;
    const content = document.getElementById('content').value;

    console.log("제목:", title);
    console.log("내용:", content);

    // 파일을 Base64로 변환 후 JSON 문자열로 묶기
    const pictures = await Promise.all(selectedFiles.map(file => {
        return new Promise((resolve, reject) => {
            const reader = new FileReader();
            reader.onload = e => resolve(e.target.result);
            reader.onerror = reject;
            reader.readAsDataURL(file);
        });
    }));

    fetch('/board/write', { 
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            title: title,
            content: content,
            pictures: pictures  // 문자열로 전송
        })
    })
    .then(response => {
        if (!response.ok) throw new Error('서버 오류 발생');
        return response.json();
    })
    .then(data => {
        console.log("서버 응답:", data);
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
