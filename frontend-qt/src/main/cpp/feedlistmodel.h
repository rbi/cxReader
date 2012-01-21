#ifndef FEEDLISTMODEL_H
#define FEEDLISTMODEL_H

#include <QAbstractListModel>
#include <QVariant>
#include <QList>
#include <QNetworkReply>
#include <QAbstractXmlReceiver>
#include <QXmlName>
#include <QXmlNamePool>
#include "config.h"

typedef struct
{
    QString name;
    QString id;
} Feed;

class FeedListModel : public QAbstractListModel, public QAbstractXmlReceiver
{
    Q_OBJECT
public:
    enum CUSTOM_DATA {
        ID = 32
    };
    explicit FeedListModel(Config * config, QObject *parent = 0);
    int rowCount(const QModelIndex &parent) const;
    QVariant data(const QModelIndex &index, int role) const;
    QVariant headerData(int section, Qt::Orientation orientation, int role) const;
    //overrided XML Stuff
    void attribute(const QXmlName &name, const QStringRef &value);
    void endElement();

    void startElement(const QXmlName &name) {}
    void atomicValue(const QVariant &) {}
    void characters(const QStringRef &) {}
    void comment(const QString &) {}
    void endDocument() {}
    void endOfSequence() {}
    void namespaceBinding(const QXmlName &) {}
    void processingInstruction(const QXmlName &, const QString &) {}
    void startDocument() {}
    void startOfSequence() {}
private:
    QList<Feed> feeds;
    Feed current;
    QXmlNamePool namePool;
    void initFeedList();
    Config *config;
signals:

public slots:

};

#endif // FEEDLISTMODEL_H
