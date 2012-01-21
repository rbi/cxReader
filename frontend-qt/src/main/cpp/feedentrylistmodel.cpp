#include <QFile>
#include <QXmlQuery>
#include "feedentrylistmodel.h"

FeedEntryListModel::FeedEntryListModel(Config * config, QString id, QObject * parent) :
    QAbstractListModel(parent), QAbstractXmlReceiver(), id(id), config(config)
{
    initFeedEntryList();
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

void FeedEntryListModel::attribute(const QXmlName &name, const QStringRef &value)
{
    if(name.localName(this->namePool) == "title")
        this->current.title = value.toString();
    else if(name.localName(this->namePool) == "summary")
        this->current.summary = value.toString();
    else if(name.localName(this->namePool) == "id")
        this->current.id = value.toString();
}

void FeedEntryListModel::endElement()
{
    if(this->feedEntries.size() != 0 && this->feedEntries.last().id == this->current.id)
        return;

    emit(this->beginInsertRows(QModelIndex(), this->feedEntries.size(), this->feedEntries.size()+1));
    this->feedEntries.append(this->current);
    emit(this->endInsertRows());
}
