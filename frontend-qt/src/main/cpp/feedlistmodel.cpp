#include <QUrl>
#include <QDomDocument>
#include <QXmlInputSource>
#include <QXmlQuery>
#include <QXmlResultItems>
#include "feedlistmodel.h"

FeedListModel::FeedListModel(QString baseUrl, QObject *parent) :
    QAbstractListModel(parent), QAbstractXmlReceiver()
{
    this->manager = new QNetworkAccessManager(this);
    connect(this->manager, SIGNAL(authenticationRequired(QNetworkReply*,QAuthenticator*)), SLOT(authenticate(QNetworkReply*,QAuthenticator*)));
    //TODO network errors

    this->baseUrl = baseUrl;
    initFeedList();
}

int FeedListModel::rowCount(const QModelIndex &parent) const
{
    return this->feeds.size();
}

QVariant FeedListModel::data(const QModelIndex &index, int role) const
{
    if(role == Qt::DisplayRole)
        return this->feeds.at(index.row()).name;
    return QVariant();
}

QVariant FeedListModel::headerData(int section, Qt::Orientation orientation, int role) const
{
    if(role == Qt::DisplayRole)
        return tr("Feeds");
    return QVariant();
}

void FeedListModel::initFeedList()
{
    QFile queryFile(":/cxReader/qml/feedList.xq");
    queryFile.open(QIODevice::ReadOnly);

    QXmlQuery query;
    query.setNetworkAccessManager(this->manager);
    query.bindVariable("file", QVariant(this->baseUrl + "/feeds/"));
    query.setQuery(&queryFile);
    this->namePool = query.namePool();
    query.evaluateTo(this);

    queryFile.close();
}

void FeedListModel::authenticate(QNetworkReply* reply, QAuthenticator* authenticator)
{
    authenticator->setUser("test");
    authenticator->setPassword("test");
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
