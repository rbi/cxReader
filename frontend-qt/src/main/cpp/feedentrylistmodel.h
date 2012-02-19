#ifndef FEEDENTRYLISTMODEL_H
#define FEEDENTRYLISTMODEL_H

#include <QAbstractListModel>
#include <QAbstractXmlReceiver>
#include <QXmlNamePool>
#include "config.h"

typedef struct
{
    QString title;
    QString summary;
    QString id;
    bool read;
} FeedEntry;

class FeedEntryListModel  : public QAbstractListModel, public QAbstractXmlReceiver
{
    Q_OBJECT
public:
    enum CUSTOM_DATA {
        SUMMARY = 32
    };
    explicit FeedEntryListModel(Config * config, QString id, QObject * parent);
    //overrides for QAbstractListModel
    int rowCount(const QModelIndex &parent) const;
    QVariant data(const QModelIndex &index, int role) const;
    QVariant headerData(int section, Qt::Orientation orientation, int role) const;
    Qt::ItemFlags flags(const QModelIndex & index) const;
    bool setData(const QModelIndex & index, const QVariant & value, int role = Qt::EditRole);
    //overrides for QAbstractXmlReceiver
    void attribute(const QXmlName &name, const QStringRef &value);
    void endElement();
    //not needed
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
    QList<FeedEntry> feedEntries;
    FeedEntry current;
    QString id;
    Config * config;
    QXmlNamePool namePool;
    void initFeedEntryList();
};

#endif // FEEDENTRYLISTMODEL_H
