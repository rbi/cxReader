#include <QUrl>
#include <QDomDocument>
#include <QXmlInputSource>
#include <QXmlQuery>
#include <QXmlResultItems>
#include "feedlistmodel.h"

FeedListModel::FeedListModel(Config *config, QObject * parent) :
    QAbstractListModel(parent), QAbstractXmlReceiver(), config(config)
{
    initFeedList();
}

int FeedListModel::rowCount(const QModelIndex &parent) const
{
    return this->feeds.size();
}
#include <QDebug>
QVariant FeedListModel::data(const QModelIndex &index, int role) const
{
    Feed feed = this->feeds.at(index.row());
    if(role == Qt::DisplayRole)
        return feed.name;
    else if(role == ID)
        return feed.id;
    return QVariant::Invalid;
}

QVariant FeedListModel::headerData(int section, Qt::Orientation orientation, int role) const
{
    if(role == Qt::DisplayRole)
        return tr("Feeds");
    return QVariant::Invalid;
}

void FeedListModel::initFeedList()
{
    QFile queryFile(":/cxReader/qml/feedList.xq");
    queryFile.open(QIODevice::ReadOnly);

    QXmlQuery query;
    query.setNetworkAccessManager(this->config->manager());
    query.bindVariable("file", QVariant(this->config->baseUrl() + "/feeds/"));
    query.setQuery(&queryFile);
    this->namePool = query.namePool();
    query.evaluateTo(this);

    queryFile.close();
}

void FeedListModel::attribute(const QXmlName &name, const QStringRef &value)
{
    if(name.localName(this->namePool) == "title")
        this->current.name = value.toString();
    else if(name.localName(this->namePool) == "id")
        this->current.id = value.toString();
}

void FeedListModel::endElement()
{
    if(this->feeds.size() != 0 && this->feeds.last().id == this->current.id)
        return;

    emit(this->beginInsertRows(QModelIndex(), this->feeds.size(), this->feeds.size()+1));
    this->feeds.append(this->current);
    emit(this->endInsertRows());
}
