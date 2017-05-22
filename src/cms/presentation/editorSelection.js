function bodyClick(event) {
    var element = event.srcElement;
    element = element.nodeType ? element : element.parentNode;
    if (!element.classList.contains('nonselectable')) {
        var inserters = document.querySelectorAll('.inserterh,.inserterv');
        for (i = inserters.length - 1; i >= 0; i--) {
            inserters[i].parentNode.removeChild(inserters[i]);
        }
        setSelected(element.id);
    } else {
        if (element.classList.contains('inserteraft')) {
            controller.insertOnAction(1);
        } else if (element.classList.contains('inserterbef')) {
            controller.insertOnAction(-1);
        } else if (element.classList.contains('inserterin')) {
            controller.insertOnAction(0);
        }
    }
}

function setSelected(elementID) {
    var selections = document.getElementsByClassName('selectedElement');
    for (i = selections.length - 1; i >= 0; i--) {
        selections[i].classList.remove('selectedElement');
    }
    element = document.getElementById(elementID);
    if (element) {
        element.classList.add('selectedElement');
        if (!element.classList.contains('nonselectable')) {
            element.innerHTML = '&lt;div class=\"inserterv nonselectable\"&gt;&lt;button class=\"inserterin nonselectable\"&gt;+&lt;/button&gt;&lt;/div&gt;' + element.innerHTML;
            if (!element.parentNode.classList.contains('nonselectable')) {
                element.innerHTML = '&lt;div class=\"inserterh nonselectable\"&gt;&lt;button class=\"inserterbef nonselectable\"&gt;+&lt;/button&gt;&lt;/div&gt;' + element.innerHTML + '&lt;div class=\"inserterh nonselectable\"&gt;&lt;button class=\"inserteraft nonselectable\"&gt;+&lt;/button&gt;&lt;/div&gt;';
            }
        }
    }
    controller.selectionChanged(element);
}

document.addEventListener('DOMContentLoaded', function () {
    document.body.addEventListener('click', bodyClick, true);
});