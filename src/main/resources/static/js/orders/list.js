function getSelectedOrderIds() {
    return Array.from(document.querySelectorAll('.order-row-checkbox:checked'))
        .map(checkbox => checkbox.dataset.id);
}

function updateListButtonsState() {
    const selectedIds = getSelectedOrderIds();

    const deleteButton = document.getElementById('btn-delete-order');
    const changeStatusButton = document.getElementById('btn-change-order-status');

    const enabled = selectedIds.length === 1;

    if (deleteButton) {
        deleteButton.disabled = !enabled;
    }

    if (changeStatusButton) {
        changeStatusButton.classList.toggle('disabled', !enabled);
    }
}

document.addEventListener('change', function (event) {
    if (!event.target.classList.contains('order-row-checkbox')) {
        return;
    }

    updateListButtonsState();
});

const deleteOrderModal = new bootstrap.Modal(
    document.getElementById('deleteOrderModal')
);

document.getElementById('btn-delete-order')?.addEventListener('click', function () {
    const selectedIds = getSelectedOrderIds();

    if (selectedIds.length !== 1) {
        return;
    }

    deleteOrderModal.show();
});

document.getElementById('btn-confirm-delete-order')?.addEventListener('click', async function () {
    const selectedIds = getSelectedOrderIds();

    if (selectedIds.length !== 1) {
        return;
    }

    try {
        const response = await fetch(`/web/orders/${selectedIds[0]}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            throw new Error('Ошибка удаления заказа');
        }
        window.location.reload();

    } catch (error) {
        console.error(error);
    }
});

document.getElementById('btn-change-order-status')?.addEventListener('click', function (event) {
    event.preventDefault();

    const selectedIds = getSelectedOrderIds();

    if (selectedIds.length !== 1) {
        return;
    }

    window.location.href =
        `/web/orders/${selectedIds[0]}/change-status/current`;
});

