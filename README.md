# Spring Boot CQRS + Event Sourcing

Projenin orjinali **AJ Catambay** tarafından hazırlanmıştır. Orijinal kodlara ulaşmak için [tıklayınız](https://github.com/ajcatambay/cqrs-bank-account-demo). Projenin Youtube'da Bridging Code kanalındaki [anlatımını](https://www.youtube.com/watch?v=SL2VSYecDvQ&ab_channel=BridgingCode) izlemenizi şiddetle tavsiye ederim.

## Genel Bakış

Proje, Spring Boot ve [Axon Framework](https://axoniq.io/) kullanılarak CQRS ve Event Sourcing design pattern'lerinin nasıl uygulanabileceğini göstermektedir.

**CQRS = Command Query Responsibility Segregation**

yani "**Komut ve Sorguların Sorumluluklarının Ayrılması**" demektir. Diğer bir deyişle, **CRUD** ifadesindeki **Read** kısmını bir yerde, **Create, Update ve Delete** kısımlarını ise başka bir yerde ele almak demektir.

Bunun için projeyi **Command, Query ve Core** olarak üçe bölmek mümkündür. Core kısmı ortak kullanılan event ve dto'lar için. Diğer ikisi de bahsi geçen komut ve sorgu kısımları için.

**Event Sourcing** ise karmaşık Enterprise uygulamaların veritabanlarını daha yönetilebilir bir şekilde ifade etmenin bir yoludur. Bu yöntemde tablolar **(entity)** değil olaylar **(event)** depolanır ve sorgu kısmında kullanılacak olan tablolar bu olaylardan türetilir.

Örneğin bu projede olduğu gibi bir banka hesabının kaydını tutmak için "hesap oluşturuldu", "hesap aktif edildi", "hesaba para yatırıldı", gibi olayları bir veritabanında tutarız. Bu olaylardan yola çıkarak, bankada kaç hesap açıldığını, her birinde ne kadar para olduğunu vs herhangi bir servis kendi erişebildiği bir veri setinde oluşturabilir ve burdan isteklere cevap verebilir.

Sorgular için kullanacağımız bir mikroservis, ayağa kalkarken bu olayları sıradan kendi veritabanına uygulayıp, kendisine lazım olan şekilde kayıtları oluşturabilir ve sorgulara buradan cevap verebilir.

## Kullanım

Proje embedded H2 veritabanı kullandığı için herhangi bir Spring Boot uygulaması gibi ayağa kaldırabilirsiniz (Application sınıfını run ederek ya da `mvn spring-boot:run` komutu ile).

**Not**: Proje Java 17 ile oluşturulmuştur. Eğer bilgisayarınızda Java 17 yoksa yükleyip kullanmaya başlamanızı öneririm 😁 ama var olan Java'nızı kullanmak için `pom.xml` dosyasındaki aşağıdaki kısmı güncellemeniz ve `record` tipindeki dosyaları `class` olarak değiştirmeniz gerekli:

``` xml
<properties>
    <java.version>17</java.version> --> burası kullandığınız java sürümüne eşit olacak. Ör: 11  
</properties>
```

Varsayılan olarak uygulama **8080** portunda ayağa kalkacak ve ihtiyacı olan tabloları oluşturacak. Uygulama ayağa kalktıktan sonra aşağıdaki örneklerle uygulamayı deneyebilirsiniz.

### Örnekler

1. Önce içinde 10.000 TL olan bir hesap açalım:
```
POST http://localhost:8080/api/v1/accounts/create
Content-Type: application/json

{
    "startingBalance": 10000
}
```

Bu istekten cevap olarak bir UUID dönecek (`c908f959-1eb4-453a-8ab7-1d956ef764d7` gibi).

2. Cevap olarak dönen id'yi kullanarak hesabımızı kontrol edelim:
```
GET http://localhost:8080/api/v1/accounts/c908f959-1eb4-453a-8ab7-1d956ef764d7
Accept: application/json
```

Bu istekten cevap olarak aşağıdaki json dönecek:
```json
{
  "accountId": "c908f959-1eb4-453a-8ab7-1d956ef764d7",
  "balance": 10000.00,
  "status": "ACTIVATED"
}
```

3. Hesabımıza para yükleyelim:
```
PUT http//localhost:8080/api/v1/accounts/deposit
Content-Type: application/json

{
    "accountId": "c908f959-1eb4-453a-8ab7-1d956ef764d7",
    "amount": 5000
}
```

Bu isteğimizden `200 OK` sonucu döndükten sonra 2 numaralı isteği tekrar çalıştırıp hesabımızda artık 15000 TL olduğunu görebiliriz. Para çekmek için de 3 numaralı istekte deposit yerine withdraw demeniz yeterli o yüzden onu ayrıca koymuyorum.

## Nasıl Çalışıyor

Peki gelelim tüm bunların nasıl çalıştığına.

Olayın temelinde Axon framework var. Bu framework ile command, query ve event'lerimizi oluşturup bunların nasıl handle edileceğini söylüyoruz.

Event'lerin tutulduğu yere Event Store deniyor. Biz şimdilik H2 veritabanı kullandık. Event Sourcing kullandığınızda bu store sizin tek gerçek kaynağınız oluyor. Diğer tüm servisler bu store'u kullanarak kendi verisini oluşturuyor.

Biz yukarıdaki örneklerden 1 numarayı çalıştırdığımızda önce istek `BankAccountController`'a gidiyor.

Burada servisimizi çağırıyoruz ve o da `CommandGateway` kullanarak komutu gönderiyor.

Bu komutu handle ettiğimiz bir yer var. O da `AccountAggregate` sınıfı. Komutu alan aggregate sınıfı hemen bir `AccountCreatedEvent` yolluyor.

Bu event, oluşan "aggregate id" yani hesabımızın id'si ile Event Store'a kaydediliyor.

Sonra yine bu sınıfta, bu create event'ini de handle ediyoruz ve bu event geldiğinde hem kendisinde bulunan id, balance ve status ifadelerini gelen isteğe göre eşitlemesini, hem de `AccountActivatedEvent` yollamasını istiyoruz.

Bu event'i de aynı sınıfta handle edip, status ifadesini event'den gelen şekilde güncellemesini istiyoruz. Bu şekilde hesabımız açılmış oluyor.

Buradaki Aggregate sınıfımız bir adet hesabı ifade ediyor. Komutları ilgili hesap için çalıştırıp kendisini ilgili hesabın son durumunu tutacak şekilde güncelliyor.

Yani olayın Command tarafı aslında `API --> Command --> Event --> Event --> ...` şeklinde gelişiyor.

Query tarafında ise bir repository'miz var ve o da H2 veritabanını kullanıyor. Kendisinin erişebildiği bir Account tablosu var. Yukarıdaki event'ler yollandıkça bu kısımdaki servisimiz de aynı aggregate sınıfı gibi handle edip repository'yi kullanarak bu tabloya ilgili verileri yansıtıyor.

Bu projede iki kısım da aynı anda ayağa kalktığı için iki kısım da aslında eş zamanlı olarak verinin son halini tutuyorlar. Command kısmı Aggregate sınıfında bu veriyi tutarken, Query kısmı Account tablosunu kullanıyor.

Ama CQRS'in asıl kendisini gösterdiği yer, bu iki kısmın ayrı mikroservisler şeklinde yayınlandığı durumdur. Bu durumda command kısmı yazmaya göre optimize edilmiş bir veritabanı kullanırken query kısmı da cache ya da no sql gibi okumaya özel bir veri tutabilir.

Böyle bir yapıda, query servisinin sonradan ayağa kalktığı durumda, event store'dan tüm event'leri okuyup tablolarını baştan oluşturabilir.