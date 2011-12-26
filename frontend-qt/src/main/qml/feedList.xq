declare namespace o = "http://www.opml.org/spec2";
declare namespace b = "http://beefochu.dyndns.org/opml-extension-feed-id";

<feeds> {
  for $x in doc($file)/o:opml/o:body/o:outline[@type="rss"]
  return <feed title="{$x/@text}" url="{$x/@xmlUrl}" id="{$x/@b:id}" />
} </feeds>
