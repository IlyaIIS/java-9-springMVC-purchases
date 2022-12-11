async function mark(id, isMarked) {
    const formData = new FormData();
    formData.append('isMarked', isMarked);

    await fetch('/api/purchases/' + id +'/mark', {
        method: 'PUT',
        body: formData
    });

    updateTable();
}

async function deletePurchase(id) {
    await fetch('/api/purchases/' + id, { method: 'DELETE' });

    updateTable();
}

async function updateTable() {
    let purchases = (await (await fetch("/api/purchases")).json()).purchases;

    let result = "";

    result += "<tr>\n<th>Удалить</th>\n<th>Покупка</th>\n<th>Метка</th>\n</tr>\n";

    purchases.forEach(el => {
        let str = "";
        str += "<tr>\n";
        str += '<th><button onclick="deletePurchase(' + el.id + ')">Удалить</button></th>\n'
        str += '<td>' + el.name + '</td>\n';
        str += '<td><button onclick="mark(' + el.id + ', ' + el.isMarked + ');" >'+ (el.isMarked ? '☑' : '☐') + '</button></td>\n';
        str += "</tr>\n";
        result += str;
    });

    result += '<tr>\n<td><button onclick="location.href=\'/home/add\'" style="float: center;">Добавить</button></td>\n</tr>\n';

    document.getElementById("table").innerHTML = result;
}