#include <QtGui/QApplication>
#include "cxreader.h"

int main(int argc, char *argv[])
{
    QApplication a(argc, argv);
    CxReader w;
    w.show();

    return a.exec();
}
