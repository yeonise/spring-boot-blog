// 삭제 기능
const deleteButton = document.getElementById('delete-btn'); // id를 delete-btn으로 설정한 엘리먼트를 찾아

if (deleteButton) {
    deleteButton.addEventListener('click', event => { // 해당 엘리먼트에서 클릭 이벤트가 발생하면
        let id = document.getElementById('article-id').value;
        fetch(`/api/articles/${id}`, { // fetch() 메서드를 통해 /api/articles/ DELETE 요청을 보내는 역할
        method: 'DELETE'
        })
        .then(() => { // fetch()가 잘 완료되면 연이어 실행되는 메서드
            alert('삭제가 완료되었습니다.');
            location.replace('/articles'); // 메서드 실행 시 사용자의 웹 브라우저 화면을 현재 주소를 기반해 옮겨주는 역할
        });
    });
}

// 수정 기능
const modifyButton = document.getElementById('modify-btn'); // id가 modify-btn인 엘리먼트 조회

if (modifyButton) {
    // 클릭 이벤트가 감지되면 수정 API 요청
    modifyButton.addEventListener('click', event => {
        let params = new URLSearchParams(location.search);
        let id = params.get('id');

        fetch(`/api/articles/${id}`, {
            method: 'PUT',
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                title: document.getElementById('title').value,
                content: document.getElementById('content').value
            })
        })
        .then(() => {
            alert('수정이 완료되었습니다.');
            location.replace(`/articles/${id}`);
        });
    });
}

// 등록 기능
const createButton = document.getElementById("create-btn");

if (createButton) {
    createButton.addEventListener("click", (event) => {
        fetch("/api/articles", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                title: document.getElementById("title").value,
                content: document.getElementById("content").value,
            }),
        })
        .then(() => {
            alert("등록이 완료되었습니다.");
            location.replace("/articles");
        });
    });
}
