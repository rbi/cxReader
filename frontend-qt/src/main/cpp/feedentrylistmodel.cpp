#include <QFile>
#include <QXmlQuery>
#include "feedentrylistmodel.h"

FeedEntryListModel::FeedEntryListModel(Config * config, QString id, QObject * parent) :
    QAbstractListModel(parent), QAbstractXmlReceiver(), id(id), config(config)
{
    initFeedEntryList();
}

void FeedEntryListModel::initFeedEntryList()
{
    QFile queryFile(":/cxReader/qml/feedEntryList.xq");
    queryFile.open(QIODevice::ReadOnly);

    QXmlQuery query;
    query.setNetworkAccessManager(this->config->manager());
    query.bindVariable("file", QVariant(this->config->baseUrl() + "/feeds/"+this->id));
    query.setQuery(&queryFile);
    this->namePool = query.namePool();
    query.evaluateTo(this);

    queryFile.close();
}

int FeedEntryListModel::rowCount(const QModelIndex &parent) const
{
    return this->feedEntries.size();
}

QVariant FeedEntryListModel::data(const QModelIndex &index, int role) const
{
    FeedEntry entry = this->feedEntries.at(index.row());
    if(role == Qt::DisplayRole)
        return entry.title;
    else if(role == Qt::CheckStateRole) {
        if(entry.read)
            return Qt::Checked;
        else
            return Qt::Unchecked;
    }
    else if(role == SUMMARY)
        return entry.summary;
    return QVariant::Invalid;
}

QVariant FeedEntryListModel::headerData(int section, Qt::Orientation orientation, int role) const
{
    if(role == Qt::DisplayRole)
        return tr("Entries");
    return QVariant::Invalid;
}

Qt::ItemFlags FeedEntryListModel::flags(const QModelIndex & index) const
{
    return QAbstractListModel::flags(index) | Qt::ItemIsUserCheckable;
}

bool FeedEntryListModel::setData(const QModelIndex & index, const QVariant & value, int role)
{
    if(role != Qt::CheckStateRole)
        return false;

    FeedEntry entry = this->feedEntries.at(index.row());
    QString checkedString;
    if(value.toInt() == Qt::Checked) {
        entry.read = true;
        checkedString = "true";
    } else {
        entry.read = false;
        checkedString = "false";
    }

    QNetworkRequest request(QUrl(this->config->baseUrl() + "/feeds/" + this->id + "/" + entry.id +
                                 "?read="+checkedString));
    this->config->manager()->put(request, QByteArray());
    this->feedEntries.replace(index.row(), entry);
    emit(dataChanged(index, index));
}



void FeedEntryListModel::attribute(const QXmlName &name, const QStringRef &value)
{
    QString tagName = name.localName(this->namePool);
    QString tagValue = value.toString();

    if(tagName == "title")
        this->current.title = tagValue;
    else if(tagName == "summary")
        this->current.summary = tagValue;
    else if(tagName == "id")
        this->current.id = tagValue;
    else if(tagName == "read") {
        if(tagValue == "true")
            this->current.read = true;
        else
            this->current.read = false;
    }
}

void FeedEntryListModel::endElement()
{
    if(this->feedEntries.size() != 0 && this->feedEntries.last().id == this->current.id)
        return;

    emit(this->beginInsertRows(QModelIndex(), this->feedEntries.size(), this->feedEntries.size()+1));
    this->feedEntries.append(this->current);
    emit(this->endInsertRows());
}
