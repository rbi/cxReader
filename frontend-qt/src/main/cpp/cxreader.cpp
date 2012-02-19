#include "cxreader.h"
#include "ui_cxreader.h"

CxReader::CxReader(Config * config, QWidget *parent) :
    QMainWindow(parent),
    ui(new Ui::CxReader), config(config), feedEntryListModel(NULL)
{
    ui->setupUi(this);
    this->ui->splitterFeedList->setSizes(QList<int>()<<50<<180);
    this->ui->splitterFeedEntryList->setSizes(QList<int>()<<50<<80);

    this->feedListModel = new FeedListModel(config, this);
    this->ui->feedList->setModel(this->feedListModel);
    connect(this->ui->feedList->selectionModel(), SIGNAL(selectionChanged(QItemSelection,QItemSelection)), SLOT(feedChanged(QItemSelection,QItemSelection)));
    connect(this->ui->unreadOnly, SIGNAL(toggled(bool)), SLOT(unreadOnlyChanged(bool)));
}

CxReader::~CxReader()
{
    if(feedEntryListModel != NULL)
        delete feedEntryListModel;
    delete feedListModel;
    delete ui;
}

void CxReader::unreadOnlyChanged(bool status)
{
    this->feedEntryListModel->showOnlyUnread(status);
}

void CxReader::feedChanged(QItemSelection selected, QItemSelection deselected)
{
    FeedEntryListModel * old = feedEntryListModel;


    QString id = this->feedListModel->data(selected.indexes().at(0), FeedListModel::ID).toString();

    this->feedEntryListModel = new FeedEntryListModel(this->config, id, this);
    this->ui->feedEntryList->setModel(this->feedEntryListModel);
    connect(this->ui->feedEntryList->selectionModel(), SIGNAL(selectionChanged(QItemSelection,QItemSelection)), SLOT(feedEntryChanged(QItemSelection, QItemSelection)));
    if(this->ui->unreadOnly->isChecked())
        this->feedEntryListModel->showOnlyUnread(true);

    if(old != NULL)
        delete old;
}

void CxReader::feedEntryChanged(QItemSelection selected, QItemSelection deselected)
{
    QString summary = this->feedEntryListModel->data(selected.indexes().at(0), FeedEntryListModel::SUMMARY).toString();
    this->ui->feedEntryContent->setHtml(summary);
}
