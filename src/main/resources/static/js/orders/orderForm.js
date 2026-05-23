const container = document.getElementById('order-items-container');
const template = document.getElementById('order-item-template');
const addButton = document.getElementById('btn-add-order-item');

function reindexOrderItems() {
    container.querySelectorAll('.order-item-row').forEach((row, index) => {
        row.querySelectorAll('[name], [data-name]').forEach(input => {
            const fieldName = input.dataset.name || input.name.replace(/^items\[\d+\]\./, '');
            input.name = `items[${index}].${fieldName}`;
        });
    });
}

function parseMoney(value) {
    if (!value) {
        return 0;
    }

    let normalized = value.toString().trim();

    // если есть и точка и запятая → определяем формат
    if (normalized.includes(',') && normalized.includes('.')) {

        // формат 4,725.00
        if (normalized.lastIndexOf('.') > normalized.lastIndexOf(',')) {
            normalized = normalized.replace(/,/g, '');
        }
        // формат 4.725,00
        else {
            normalized = normalized.replace(/\./g, '').replace(',', '.');
        }

    } else if (normalized.includes(',')) {
        // формат 4725,00
        normalized = normalized.replace(',', '.');
    }

    normalized = normalized.replace(/\s/g, '');

    return Number(normalized) || 0;
}

function formatMoney(value) {
    if (value == null || value === "") {
        return 0;
    }
    return value.toLocaleString('ru-RU', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    });
}

function recalcRow(row) {
    const quantity = parseMoney(row.querySelector('.item-quantity')?.value);
    const price = parseMoney(row.querySelector('.item-price')?.value);
    const discount = parseMoney(row.querySelector('.item-discount')?.value);

    const amount = quantity * price * (100 - discount) / 100;

    const amountInput = row.querySelector('.item-amount');
    if (amountInput) {
        amountInput.value = formatMoney(amount);
    }
}

function recalcTotals() {
    let total = 0;
    let supplierTotal = 0;

    const postpay = parseMoney(document.getElementById('input-amounts-postpay')?.value);
    container.querySelectorAll('.order-item-row').forEach(row => {
        const quantity = parseMoney(row.querySelector('.item-quantity')?.value);
        const price = parseMoney(row.querySelector('.item-price')?.value);
        const supplierPrice = parseMoney(row.querySelector('.item-supplier-price')?.value);
        const discount = parseMoney(row.querySelector('.item-discount')?.value);

        total += quantity * price * (100 - discount) / 100;
        supplierTotal += quantity * supplierPrice;
    });

    const margin = total - supplierTotal;
    const deliveryInput = document.getElementById('input-delivery-price');
    const deliveryAmount = deliveryInput ? parseMoney(deliveryInput.value) : 0;

    document.getElementById('details-items-amounts-total').textContent = formatMoney(total);
    document.getElementById('details-items-amounts-delivery').textContent = formatMoney(deliveryAmount);
    document.getElementById('details-items-amounts-total-with-delivery').textContent = formatMoney(total + deliveryAmount);

    document.getElementById('details-items-amounts-supplier').textContent = formatMoney(supplierTotal);
    document.getElementById('details-items-amounts-margin').textContent = formatMoney(margin);
    document.getElementById('details-items-amounts-postpay').textContent = formatMoney(postpay);
}

function addOrderItemRow() {
    const fragment = template.content.cloneNode(true);
    container.appendChild(fragment);

    reindexOrderItems();

    const lastRow = container.querySelector('.order-item-row:last-child');
    if (lastRow) {
        recalcRow(lastRow);
        recalcTotals();
    }
}

addButton?.addEventListener('click', addOrderItemRow);

container.addEventListener('click', function (event) {
    const removeButton = event.target.closest('.btn-remove-order-item');

    if (!removeButton) {
        return;
    }

    removeButton.closest('.order-item-row').remove();
    reindexOrderItems();
});

container.addEventListener('input', function (event) {
    const row = event.target.closest('.order-item-row');

    if (!row) {
        return;
    }

    if (
        event.target.classList.contains('item-quantity') ||
        event.target.classList.contains('item-price') ||
        event.target.classList.contains('item-discount')
    ) {
        recalcRow(row);
        recalcTotals();
    }
});

function normalizeMoneyForSubmit(value) {
    if (!value) {
        return '';
    }

    return value.toString()
        .replace(/\u00A0/g, '')
        .replace(/\s/g, '')
        .replace(',', '.');
}

document.querySelector('form').addEventListener('submit', function () {
    reindexOrderItems();

    container.querySelectorAll('.order-item-row').forEach(row => {
        recalcRow(row);
        recalcTotals();
    });

    document.querySelectorAll('.item-price, .item-supplier-price, .item-amount').forEach(input => {
        input.value = normalizeMoneyForSubmit(input.value);
    });

    document.querySelectorAll('.item-discount').forEach(input => {
        input.value = parseInt(input.value || '0', 10);
    });

    const deliveryPriceInput = document.getElementById('input-delivery-price');
    if (deliveryPriceInput) {
        deliveryPriceInput.value = normalizeMoneyForSubmit(deliveryPriceInput.value);
    }

    const postpayAmountInput = document.getElementById('input-amounts-postpay');
    if (postpayAmountInput) {
        postpayAmountInput.value = normalizeMoneyForSubmit(postpayAmountInput.value);
    }

    $('.input-mask-phone').each(function () {
        $(this).val($(this).cleanVal());
    });

    const customerBlock = document.querySelector('.form-group-customer');
    const companyBlock = document.querySelector('.form-group-company');

    if (customerBlock.classList.contains('d-none')) {
        customerBlock.querySelectorAll('input, select, textarea').forEach(el => el.disabled = true);
    }

    if (companyBlock.classList.contains('d-none')) {
        companyBlock.querySelectorAll('input, select, textarea').forEach(el => el.disabled = true);
    }
});

document.addEventListener('DOMContentLoaded', function () {
    container.querySelectorAll('.order-item-row').forEach(row => {
        recalcRow(row);
    });
    recalcTotals();
});

async function findProductSuggest(query) {
    const response = await fetch(
        '/web/wiki/products/suggest?nameContext=' + encodeURIComponent(query),
        {
            method: 'GET',
            headers: {
                'Accept': 'application/json'
            }
        }
    );

    if (!response.ok) {
        throw new Error('Ошибка поиска товара');
    }

    return await response.json();
}

container.addEventListener('click', async function (event) {
    const findButton = event.target.closest('.btn-find-details-item-product');

    if (!findButton) {
        return;
    }

    const row = findButton.closest('.order-item-row');

    if (!row) {
        return;
    }

    const productNameInput = row.querySelector('input[name$=".product.shortName"]');
    const productIdInput = row.querySelector('.input-product-id');
    const productSkuInput = row.querySelector('.input-product-sku');
    const priceInput = row.querySelector('.item-price');
    const supplierPriceInput = row.querySelector('.item-supplier-price');

    const query = productNameInput?.value?.trim();

    if (!query || query.length < 2) {
        alert('Введите минимум 2 символа для поиска товара');
        return;
    }

    try {
        const result = await findProductSuggest(query);

        if (!result.items || result.items.length === 0) {
            alert('Товар не найден');
            return;
        }

        const productSkuBadge = row.querySelector('.span-product-sku');
        const productStockBadge = row.querySelector('.span-product-stock-info');
        const product = result.items[0];

        if (productIdInput) {
            productIdInput.value = product.id || '';
        }

        if (productSkuInput) {
            productSkuInput.value = product.sku || '';
        }

        if (productNameInput) {
            productNameInput.value = product.shortName || '';
        }

        if (priceInput && product.price != null) {
            priceInput.value = formatMoney(product.price);
        }

        if (supplierPriceInput && product.stock?.supplierPrice != null) {
            supplierPriceInput.value = formatMoney(product.stock.supplierPrice);
        }

        if (productSkuBadge) {
            productSkuBadge.textContent = product.sku || '';
        }

        if (productStockBadge) {
            productStockBadge.textContent = product.viewStockQuantityText || '';

            productStockBadge.className = 'badge mt-1 span-product-stock-info text-bg-' +
                (product.viewStockQuantityClass || 'light');
        }

        recalcRow(row);
        recalcTotals();

    } catch (error) {
        console.error(error);
        alert('Ошибка поиска товара');
    }
});

function toggleDeliveryRecipientFields() {
    const checkbox = document.getElementById('check-delivery-customer-equals-recipient');
    const recipientInputs = document.querySelectorAll('.input-delivery-recipient');

    if (!checkbox) {
        return;
    }

    recipientInputs.forEach(input => {
        input.disabled = checkbox.checked;

        if (checkbox.checked) {
            input.classList.add('bg-light');
        } else {
            input.classList.remove('bg-light');
        }
    });
}

document.addEventListener('DOMContentLoaded', function () {
    const checkbox = document.getElementById('check-delivery-customer-equals-recipient');

    toggleDeliveryRecipientFields();

    checkbox.addEventListener('change', toggleDeliveryRecipientFields);
});

$(document).ready(function () {
    $('.input-mask-phone').mask('(000) 000-00-00');
    $('.input-mask-time').mask('00:00');
});


let cdekWidget;

document.addEventListener('DOMContentLoaded', function () {
    const cdekButton = document.getElementById('btn-cdek-widget-1');

    if (!cdekButton) {
        return;
    }

    cdekButton.addEventListener('click', function () {
        if (cdekWidget) {
            return;
        }

        cdekWidget = new window.CDEKWidget({
            from: {
                code: 44
            },
            defaultLocation: 'Москва',
            root: 'cdek-map',
            apiKey: 'YANDEX_MAP_API_KEY',
            servicePath: '/web/wiki/integrations/cdek/widget',
            canChoose: true,
            hideFilters: false,
            hideDeliveryOptions: false,
            debug: true,

            onChoose: function (deliveryType, tariff, address) {
                console.log('deliveryType', deliveryType);
                console.log('tariff', tariff);
                console.log('address', address);
            }
        });
    });
});


function updateCustomerHeader() {
    const selectedRadio = document.querySelector('.radio-customer-type:checked');
    const header = document.getElementById('header-group-customer');

    if (!selectedRadio || !header) {
        return;
    }

    const labels = {
        PERSON: 'Покупатель (физическое лицо)',
        COMPANY: 'Покупатель (юридическое лицо)',
        BUSINESSMAN: 'Покупатель (индивидуальный предприниматель)',
        FOREIGNER_PERSON: 'Покупатель (нерезидент, физическое лицо)',
        FOREIGNER_COMPANY: 'Покупатель (нерезидент, юридическое лицо)'
    };

    header.textContent = labels[selectedRadio.value] || 'Покупатель';
}

document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('.radio-customer-type').forEach(radio => {
        radio.addEventListener('change', updateCustomerHeader);
    });

    updateCustomerHeader();
});

function toggleCustomerTypeBlocks() {
    const selectedRadio = document.querySelector('.radio-customer-type:checked');
    const customerBlock = document.querySelector('.form-group-customer');
    const companyBlock = document.querySelector('.form-group-company');

    if (!selectedRadio || !customerBlock || !companyBlock) {
        return;
    }

    const personTypes = ['PERSON', 'FOREIGNER_PERSON'];
    const companyTypes = ['COMPANY', 'BUSINESSMAN', 'FOREIGNER_COMPANY'];

    const selectedType = selectedRadio.value;

    customerBlock.classList.toggle('d-none', !personTypes.includes(selectedType));
    companyBlock.classList.toggle('d-none', !companyTypes.includes(selectedType));
}

document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('.radio-customer-type').forEach(radio => {
        radio.addEventListener('change', function () {
            updateCustomerHeader();
            toggleCustomerTypeBlocks();
        });
    });

    updateCustomerHeader();
    toggleCustomerTypeBlocks();
});

document.addEventListener('DOMContentLoaded', function () {
    const findCustomerButton = document.getElementById('btn-find-customer-phone');

    if (!findCustomerButton) {
        return;
    }

    findCustomerButton.addEventListener('click', async function () {
        const phoneInput = document.getElementById('input-customer-phone');
        const selectedCustomerType = document.querySelector('.radio-customer-type:checked');

        if (!phoneInput || !selectedCustomerType) {
            return;
        }

        const phoneNumber = phoneInput.value.trim();

        if (!phoneNumber) {
            return;
        }

        const requestBody = {
            pageNumber: 1,
            resultsOnPage: 10,
            conditions: {
                customerTypes: [selectedCustomerType.value],
                personPhoneNumber: phoneNumber
            }
        };

        try {
            const response = await fetch('/web/wiki/integrations/union/customers/suggest', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify(requestBody)
            });

            if (!response.ok) {
                throw new Error('Ошибка поиска клиента');
            }

            const result = await response.json();
            console.log('Customer suggest response:', result);
            renderCustomerOrders(result.orders || []);

            const customerId = result.customer?.id || '';
            document.querySelector('input[name="formCustomerId"]').value = result.customer?.id || '';
            document.querySelector('input[name="formCustomerPersonId"]').value = result.customer?.person?.id || '';

            if (result.customer?.id) {
                setIfBlank('#input-customer-first-name', result.customer?.person?.firstName);
                setIfBlank('#input-customer-middle-name', result.customer?.person?.middleName);
                setIfBlank('#input-customer-last-name', result.customer?.person?.lastName);
                setIfBlank('#input-customer-email', result.customer?.person?.email);
            }
            const customerBadge = document.getElementById('badge-customer-id');
            if (customerBadge) {
                customerBadge.textContent = customerId || '0';
            }

        } catch (error) {
            console.error('Ошибка запроса customer suggest:', error);
        }
    });
});

document.addEventListener('DOMContentLoaded', function () {

    const btnDeliveryCdekOk = document.getElementById('button-modal-delivery-cdek-ok');
    const deliveryCdekModalElement = document.getElementById('delivery-cdek-modal');

    if (btnDeliveryCdekOk && deliveryCdekModalElement) {

        btnDeliveryCdekOk.addEventListener('click', function () {

            const selectedCity = document.getElementById('select-delivery-cdek-city')?.value || '';
            const selectedPvz = document.getElementById('select-delivery-cdek-pvz')?.value || '';

            const street = document.getElementById('input-delivery-cdek-street')?.value || '';
            const house = document.getElementById('input-delivery-cdek-house')?.value || '';
            const flat = document.getElementById('input-delivery-cdek-flat')?.value || '';
            const fullAddress = document.getElementById('input-delivery-cdek-city-address')?.value || '';

            console.log('CDEK delivery selected:', {
                city: selectedCity,
                pvz: selectedPvz,
                street: street,
                house: house,
                flat: flat,
                address: fullAddress
            });

            //document.getElementById('input-delivery-address-city').value = document.getElementById('input-delivery-cdek-city-search').value.trim();
            document.getElementById('input-delivery-address-city-code').value = document.getElementById('select-delivery-cdek-city').value;
            document.getElementById('input-delivery-point-code').value = document.getElementById('select-delivery-cdek-pvz').value;

            const selectElement = document.getElementById('select-delivery-cdek-pvz');
            document.getElementById('input-delivery-address').value = selectElement.options[selectElement.selectedIndex].text;

            /*
             TODO:
             Здесь будет:
             - запись данных в форму заказа
             - расчет стоимости доставки
             - обновление UI
            */

            const modalInstance = bootstrap.Modal.getInstance(deliveryCdekModalElement);
            if (modalInstance) {
                modalInstance.hide();
            }
        });
    }

});


document.getElementById('btn-find-delivery-cdek-city')?.addEventListener('click', async function () {
    const cityName = document.getElementById('input-delivery-cdek-city-search').value.trim();
    const citySelect = document.getElementById('select-delivery-cdek-city');

    if (!cityName) {
        console.warn('Город не указан');
        return;
    }

    try {
        const response = await fetch(
            `/web/wiki/integrations/cdek/location/cities?countryCode2=RU&cityNameContext=${encodeURIComponent(cityName)}`,
            {
                method: 'GET',
                headers: {
                    'Accept': 'application/json'
                }
            }
        );

        if (!response.ok) {
            throw new Error(`Ошибка HTTP: ${response.status}`);
        }

        const result = await response.json();
        console.log('Города СДЭК:', result);

        citySelect.innerHTML = '';
        if (!result.items || result.items.length === 0) {
            const option = document.createElement('option');
            option.value = '';
            option.textContent = 'Города не найдены';
            citySelect.appendChild(option);
            return;
        }

        result.items.forEach(city => {
            const option = document.createElement('option');

            option.value = city.code;
            option.textContent = `${city.city}, ${city.region || ''}, ${city.country}`;

            option.dataset.city = city.city;
            option.dataset.region = city.region || '';
            option.dataset.country = city.country;
            option.dataset.code = city.code;
            option.dataset.cityUuid = city.city_uuid || '';
            option.dataset.fiasGuid = city.fias_guid || '';
            citySelect.appendChild(option);
        });
        citySelect.selectedIndex = 0;

        if (result.items.length === 1) {
            await loadCdekDeliveryPoints();
        }

    } catch (error) {
        console.error('Ошибка поиска города СДЭК:', error);
    }
});


document.getElementById('select-delivery-cdek-city')?.addEventListener('change', loadCdekDeliveryPoints);

async function loadCdekDeliveryPoints() {
    const citySelect = document.getElementById('select-delivery-cdek-city');
    const pvzSelect = document.getElementById('select-delivery-cdek-pvz');

    const cityCode = citySelect.value;

    if (!cityCode) {
        return;
    }

    const selectedOption = citySelect.options[citySelect.selectedIndex];
    document.getElementById('input-delivery-address-city').value = selectedOption?.dataset?.city || '';
    pvzSelect.innerHTML = '';

    try {
        const response = await fetch(
            `/web/wiki/integrations/cdek/deliveryPoints?countryCode2=RU&cityCode=${encodeURIComponent(cityCode)}`,
            {
                method: 'GET',
                headers: {
                    'Accept': 'application/json'
                }
            }
        );

        if (!response.ok) {
            throw new Error(`Ошибка HTTP: ${response.status}`);
        }

        const result = await response.json();
        console.log('ПВЗ СДЭК:', result);

        if (result.result && result.result !== 'ok') {
            throw new Error(result.errorMessage || 'Ошибка получения ПВЗ СДЭК');
        }

        const items = Array.isArray(result)
            ? result
            : Array.isArray(result.items)
                ? result.items
                : [];

        if (items.length === 0) {
            const option = document.createElement('option');
            option.value = '';
            option.textContent = 'ПВЗ не найдены';
            pvzSelect.appendChild(option);
            return;
        }

        items.forEach(pvz => {
            const option = document.createElement('option');

            const address = pvz.location?.address || pvz.location?.addressFull || '';

            option.value = pvz.code || '';
            option.textContent = `[${pvz.code}] ${address}`;

            option.dataset.code = pvz.code || '';
            option.dataset.uuid = pvz.uuid || '';
            option.dataset.name = pvz.name || '';
            option.dataset.address = address;
            option.dataset.city = pvz.location?.city || '';
            option.dataset.region = pvz.location?.region || '';

            pvzSelect.appendChild(option);
        });

    } catch (error) {
        console.error('Ошибка загрузки ПВЗ СДЭК:', error);
    }
}

function renderCustomerOrders(orders) {
    const badge = document.getElementById('badge-customer-orders-count');
    const menu = document.getElementById('dropdown-customer-orders');

    if (!badge || !menu) {
        return;
    }

    badge.textContent = orders.length;
    menu.innerHTML = '';

    if (orders.length === 0) {
        const item = document.createElement('li');
        item.innerHTML = '<span class="dropdown-item text-muted">Заказов не найдено</span>';
        menu.appendChild(item);
        return;
    }

    orders.forEach(order => {
        const itemNames = (order.items || [])
            .map(item => `${item.product?.shortName || ''}: ${item.quantity || 0} шт`)
            .join('; ');

        const amount = order.amounts?.TOTAL_WITH_DELIVERY ?? order.amounts?.TOTAL ?? 0;
        const status = order.status?.annotation || '';
        const orderDate = formatIsoDateRu(order.orderDate);

        const li = document.createElement('li');
        const link = document.createElement('a');

        link.className = 'dropdown-item small customer-order-dropdown-item';
        link.href = `/web/orders/${order.id}/show`;
        link.textContent = `${order.viewNum || order.orderNum} ${orderDate} ${formatMoney(amount)} ${status} ${itemNames}`;

        li.appendChild(link);
        menu.appendChild(li);
    });
}

function formatIsoDateRu(value) {
    if (!value) {
        return '';
    }

    const parts = value.split('-');
    if (parts.length !== 3) {
        return value;
    }

    return `${parts[2]}.${parts[1]}.${parts[0]}`;
}

document.getElementById('btn-find-customer-orders')?.addEventListener('click', function () {
    const menu = document.getElementById('dropdown-customer-orders');

    if (!menu || menu.children.length === 0) {
        return;
    }
});

document.getElementById('btn-calc-parcel-delivery-amounts')?.addEventListener('click', calcParcelDeliveryAmounts);

async function calcParcelDeliveryAmounts() {
    const requestBody = {
        order: buildOrderCalcRequest()
    };

    console.log(JSON.stringify(requestBody, null, 2));

    try {
        const response = await fetch('/web/wiki/delivery/calc/parcel-delivery-amounts', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify(requestBody)
        });

        if (!response.ok) {
            const errorBody = await response.text();
            console.error('Delivery calc error response:', errorBody);
            throw new Error(`Ошибка расчета доставки: ${response.status}`);
        }

        const data = await response.json();
        const deliveryCalcParcel = data.deliveryCalcParcel;

        if (!deliveryCalcParcel) {
            alert('Расчет доставки не вернул результат');
            return;
        }
        console.log(JSON.stringify(deliveryCalcParcel, null, 2));

        applyDeliveryCalcResult(deliveryCalcParcel);

    } catch (error) {
        console.error('Ошибка расчета доставки:', error);
        alert('Ошибка расчета доставки');
    }
}

function buildOrderCalcRequest() {
    return {
        orderNum: Number(document.getElementById('input-order-no')?.value || 0),
        orderDate: toIsoDate(document.getElementById('input-order-orderDate')?.value),
        type: document.getElementById('select-order-type')?.value,
        sourceType: document.getElementById('select-order-source-type')?.value,
        advertType: document.getElementById('select-order-advert-type')?.value,
        paymentType: document.getElementById('select-payment-type')?.value,
        store: document.getElementById('select-store')?.value,
        customerId: Number(document.querySelector('input[name="formCustomerId"]')?.value || 0),
        productCategoryId: Number(document.getElementById('select-order-product-category')?.value || 0),

        delivery: {
            deliveryType: document.getElementById('select-delivery-type')?.value,
            deliveryPaymentType: document.getElementById('select-payment-delivery-type')?.value,
            price: parseMoney(document.getElementById('input-delivery-price')?.value),

            address: {
                type: 'MAIN',
                countryId: document.getElementById('select-delivery-address-country')?.value,
                cityCode: document.getElementById('input-delivery-address-city-code')?.value || '',
                city: document.getElementById('input-delivery-address-city')?.value || '',
                deliveryPointCode: document.getElementById('input-delivery-point-code')?.value || '',
                // cityCode: Number(document.getElementById('select-delivery-cdek-city')?.value || 0),
                //city: 'Псков',
                addressLine: document.getElementById('input-delivery-address')?.value || ''
            },

            recipient: {
                firstName: document.getElementById('input-delivery-recipient-first-name')?.value || '',
                middleName: document.getElementById('input-delivery-recipient-middle-name')?.value || '',
                lastName: document.getElementById('input-delivery-recipient-last-name')?.value || '',
                phoneNumber: document.getElementById('input-delivery-recipient-phone-number')?.value || ''
            },

            annotation: document.getElementById('input-delivery-courier-annotation')?.value || ''
        },

        items: buildOrderItemsCalcRequest(),

        annotation: document.getElementById('input-payment-annotation')?.value || ''
    };
}

function buildOrderItemsCalcRequest() {
    const items = [];

    container.querySelectorAll('.order-item-row').forEach((row, index) => {
        const productId = row.querySelector('.input-product-id')?.value;

        if (!productId) {
            return;
        }

        items.push({
            itemNum: index + 1,
            productId: Number(productId),
            price: parseMoney(row.querySelector('.item-price')?.value),
            supplierPrice: parseMoney(row.querySelector('.item-supplier-price')?.value),
            quantity: parseMoney(row.querySelector('.item-quantity')?.value),
            discountRate: parseMoney(row.querySelector('.item-discount')?.value)
        });
    });

    return items;
}

function applyDeliveryCalcResult(deliveryCalcParcel) {

    document.getElementById('input-amounts-postpay').value = formatMoney(deliveryCalcParcel.postpayAmount);
    document.getElementById('input-delivery-price').value = formatMoney(deliveryCalcParcel.deliveryFullPrice);
    document.getElementById('details-items-amounts-delivery-seller-summary').textContent = formatMoney(deliveryCalcParcel.deliverySellerSummary);

    document.getElementById('details-items-amounts-delivery-customer-summary').textContent = formatMoney(deliveryCalcParcel.deliveryCustomerSummary);
    document.getElementById('details-items-amounts-bill').textContent = document.getElementById('details-items-amounts-total-with-delivery').textContent;

    const infoButton = document.getElementById('btn-calc-parcel-delivery-amounts-info');
    if (infoButton) {
        const popover = bootstrap.Popover.getOrCreateInstance(infoButton);
        popover.setContent({
            '.popover-header': deliveryCalcParcel.parcelType || 'Доставка',
            '.popover-body': deliveryCalcParcel.info || ''
        });
        popover.show();
    }
    recalcTotals();
}

function toIsoDate(value) {
    if (!value) {
        return null;
    }

    const parts = value.trim().split('.');
    if (parts.length !== 3) {
        return value;
    }

    return `${parts[2]}-${parts[1]}-${parts[0]}`;
}

function setIfBlank(selector, value) {
    const element = document.querySelector(selector);

    if (!element) {
        return;
    }

    if (element.value && element.value.trim() !== '') {
        return;
    }

    element.value = value || '';
}
