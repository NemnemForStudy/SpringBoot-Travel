document.addEventListener("DOMContentLoaded", () => {
    async function top3Cards() {
        try {
            const res = await fetch("/api/cards/top3");
            const data = await res.json();

            const cardContainer = document.getElementById("top3CardsContainer");
            const carouselContainer = document.querySelector("#carouselCaptions .carousel-inner");
            const carouselIndicators = document.querySelector("#carouselCaptions .carousel-indicators");

            cardContainer.innerHTML = ""; // 기존 카드 내용 제거
            carouselContainer.innerHTML = ""; // 기존 슬라이드 제거
            carouselIndicators.innerHTML = ""; // 기존 인디케이터 제거

            const items = Array.isArray(data) ? data.slice(0, 3) : [data];

            items.forEach((item, index) => {
                // ---------------- 카드 생성 ----------------
                const col = document.createElement('div');
                col.classList.add('col');

                const card = document.createElement('div');
                card.classList.add('card');

                // 카드 클릭 시 이동
                card.style.cursor = 'pointer';
                card.addEventListener("click", () => {
                    window.location.href = `/board/boardDetailForm/${item.boardId}`;
                });

                const img = document.createElement('img');
                img.src = item.picture;
                img.classList.add('card-img-top', 'card-img');

                const cardBody = document.createElement('div');
                cardBody.classList.add('card-body');

                const title = document.createElement('h5');
                title.classList.add('card-title');
                title.textContent = item.title;

                const content = document.createElement('p');
                content.classList.add('card-text');
                content.textContent = item.content;

                cardBody.appendChild(title);
                cardBody.appendChild(content);
                card.appendChild(img);
                card.appendChild(cardBody);
                col.appendChild(card);
                cardContainer.appendChild(col);

                // ---------------- Carousel 슬라이드 생성 ----------------
                const carouselItem = document.createElement('div');
                carouselItem.classList.add('carousel-item');
                if (index === 0) carouselItem.classList.add('active'); // 첫 번째 슬라이드 활성화

                const carouselImg = document.createElement('img');
                carouselImg.src = item.picture;
                carouselImg.classList.add('d-block', 'w-100');
                carouselImg.style.height = '400px';
                carouselImg.style.objectFit = 'contain';
                carouselImg.alt = item.title;

                const carouselCaption = document.createElement('div');
                carouselCaption.classList.add('carousel-caption', 'd-none', 'd-md-block');

                const carouselTitle = document.createElement('h5');
                carouselTitle.textContent = item.title;
                const carouselText = document.createElement('p');
                carouselText.textContent = item.content;

                carouselCaption.appendChild(carouselTitle);
                carouselCaption.appendChild(carouselText);

                carouselItem.appendChild(carouselImg);
                carouselItem.appendChild(carouselCaption);
                carouselContainer.appendChild(carouselItem);

                // Carousel indicators
                const indicator = document.createElement('button');
                indicator.type = 'button';
                indicator.setAttribute('data-bs-target', '#carouselCaptions');
                indicator.setAttribute('data-bs-slide-to', index);
                if (index === 0) {
                    indicator.classList.add('active');
                    indicator.setAttribute('aria-current', 'true');
                }
                indicator.setAttribute('aria-label', `Slide ${index + 1}`);
                carouselIndicators.appendChild(indicator);
            });

        } catch(err) {
            console.error("로드 실패", err);
        }
    }

    top3Cards();
});
