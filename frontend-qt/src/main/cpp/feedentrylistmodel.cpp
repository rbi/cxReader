#include <QFile>
#include <QXmlQuery>
#include "feedentrylistmodel.h"

FeedEntryListModel::FeedEntryListModel(Config * config, QString id, QObject * parent) :
    QAbstractListModel(parent), QAbstractXmlReceiver(), id(id), config(config), currentFeedEntries(&feedEntriesAll)
{
    initFeedEntryList();
}

void FeedEntryListModel::showOnlyUnread(bool unreadOnly)
{
    QList<FeedEntry*> * oldList = this->currentFeedEntries;
    QList<FeedEntry*> * newList = &this->feedEntriesAll;
    int oldSize = oldList->size();
    int newSize = newList->size();

    if(unreadOnly)
        newList = &this->feedEntriesUnread;

    if(oldList == newList)
        return;

    if(newSize > oldSize)
        emit(this->beginInsertRows(QModelIndex(), oldSize, newSize));
    else if(newSize < oldSize)
        emit(this->beginRemoveRows(QModelIndex(), newSize, oldSize));

    this->currentFeedEntries = newList;

    if(newSize > oldSize) {
        emit(this->endInsertRows());
        emit(this->dataChanged(this->index(0), this->index(oldSize - 1)));
    } else if(newSize < oldSize)
        emit(this->endRemoveRows());

    emit(this->dataChanged(this->index(0), this->index(newSize -1)));
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

void FeedEntryListModel::rebuildUnreadEntriesList()
{
    this->feedEntriesUnread.clear();
    for(int i = 0; i < this->feedEntriesAll.size(); i++) {
        FeedEntry * candidate = this->feedEntriesAll.at(i);
        if(!candidate->read)
            this->feedEntriesUnread.append(candidate);
    }
}

int FeedEntryListModel::rowCount(const QModelIndex &parent) const
{
    return this->currentFeedEntries->size();
}

QVariant FeedEntryListModel::data(const QModelIndex &index, int role) const
{
    FeedEntry entry = *this->currentFeedEntries->at(index.row());
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

    FeedEntry * entry = this->currentFeedEntries->at(index.row());
    QString checkedString;
    if(value.toInt() == Qt::Checked) {
        entry->read = true;
        checkedString = "true";
    } else {
        entry->read = false;
        checkedString = "false";
    }

    QNetworkRequest request(QUrl(this->config->baseUrl() + "/feeds/" + this->id + "/" + entry->id +
                                 "?read="+checkedString));
    this->config->manager()->put(request, QByteArray());

    if(this->currentFeedEntries == &this->feedEntriesAll) {
        //TODO remove don't rebuild, simply remove or insert after last unread entry of feedEntriesAll
        rebuildUnreadEntriesList();
        emit(dataChanged(index, index));
    } else {
        emit(this->beginRemoveRows(QModelIndex(), index.row(), index.row()));
        this->feedEntriesUnread.removeOne(entry);
        emit(this->endRemoveRows());
    }
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

    if(this->current.read || (this->currentFeedEntries == &this->feedEntriesAll))
        emit(this->beginInsertRows(QModelIndex(), this->currentFeedEntries->size(), this->currentFeedEntries->size()+1));

    this->feedEntries.append(this->current);
    this->feedEntriesAll.append(&this->feedEntries.last());
    if(!this->current.read)
        this->feedEntriesUnread.append(&this->feedEntries.last());

    if(this->current.read || (this->currentFeedEntries == &this->feedEntriesAll))
        emit(this->endInsertRows());
}
