function toggleSidebar() {
    const sidebar = document.querySelector('.sidebar');
    const toggleIcon = document.querySelector('.toggle-sidebar');

    sidebar.classList.toggle('collapsed');

    if (sidebar.classList.contains('collapsed')) {
        toggleIcon.classList.remove('pi-chevron-left');
        toggleIcon.classList.add('pi-chevron-right');
    } else {
        toggleIcon.classList.remove('pi-chevron-right');
        toggleIcon.classList.add('pi-chevron-left');
    }

    // Reset margin for active item when sidebar expands
    updateActiveItemMargin();
}

document.addEventListener("click", function (event) {
    if (event.target.closest('.sidebar-menu li')) {
        document.querySelectorAll('.sidebar-menu li').forEach(item => {
            item.classList.remove('active');
            item.style.margin = "0"; // Reset all margins
        });

        const clickedItem = event.target.closest('.sidebar-menu li');
        clickedItem.classList.add('active');

        updateActiveItemMargin();
    }
});

function updateActiveItemMargin() {
    const sidebar = document.querySelector('.sidebar');
    const activeItem = document.querySelector('.sidebar-menu li.active');

    if (activeItem) {
        if (sidebar.classList.contains('collapsed')) {
            activeItem.style.margin = "0"; // No margin when collapsed
        } else {
            activeItem.style.margin = "0px 0px 0px 30px"; // Apply margin when expanded
        }
    }
}


