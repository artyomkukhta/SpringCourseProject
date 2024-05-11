function formatCardNumber() {
    let cardNumberInput = document.getElementById('cardNumber');
    let cursorPosition = cardNumberInput.selectionStart; // Сохраняем позицию курсора
    let cardNumber = cardNumberInput.value.replace(/\s+/g, ''); // Удаляем пробелы из введенного номера
    let formattedCardNumber = '';
    let cursorOffset = 0; // Смещение курсора после добавления пробелов
    for (let i = 0; i < cardNumber.length; i++) {
        if (i % 4 === 0 && i !== 0) { // Добавляем пробел после каждой четвертой цифры, кроме первой
            formattedCardNumber += ' ';
            cursorOffset++; // Увеличиваем смещение курсора
        }
        formattedCardNumber += cardNumber.charAt(i);
    }
    cardNumberInput.value = formattedCardNumber;

    // Восстанавливаем позицию курсора после изменения значения поля ввода
    cardNumberInput.setSelectionRange(cursorPosition + cursorOffset, cursorPosition + cursorOffset);
}