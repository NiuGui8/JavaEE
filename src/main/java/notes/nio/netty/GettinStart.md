
### 开始

Netty项目致力于提供异步事件驱动的网络应用框架和工具，以快速开发可维护的高性能、高可伸缩性协议服务器和客户端。


换句话说，Netty是一个NIO客户端服务器框架，它支持快速和简单地开发网络应用程序，如协议服务器和客户端。它极大地简化了和流程化了网络编程，如TCP和UDP套接字服务器开发。


“快速和简单”并不意味着最终的应用程序将受到可维护性或性能问题的影响。Netty经过精心设计，吸取了许多协议(如FTP、SMTP、HTTP和各种基于二进制和文本的遗留协议)实现的经验。因此，Netty成功地找到了一种方法，在不妥协的情况下实现开发的易用性、性能、稳定性和灵活性。


一些用户可能已经发现了其他声称具有相同优势的网络应用程序框架，您可能想问Netty与他们有什么不同。答案是它所基于的哲学。Netty从一开始就为您提供API和实现方面最舒适的体验。这不是什么有形的东西，但当你阅读本指南并与Netty一起玩时，你会意识到这种哲学将使你的生活更容易。

---

### 编写一个 [Discard](https://tools.ietf.org/html/rfc863) Server
最简单的协议不是 `Hello world` ，而是 [Discard](https://tools.ietf.org/html/rfc863), 它是一个丢弃任何接受到的数据而没有响应的协议。

要实现 `Discard` 协议，我们需要做的就是丢弃所有接受到的消息，让我们直接从处理Netty生成的I/O事件的处理程序实现类开始。

	package io.netty.example.discard;
	
	import io.netty.buffer.ByteBuf;
	
	import io.netty.channel.ChannelHandlerContext;
	import io.netty.channel.ChannelInboundHandlerAdapter;
	
	/**
	 * Handles a server-side channel.
	 */
	public class DiscardServerHandler extends ChannelInboundHandlerAdapter { // (1)
	
	    @Override
	    public void channelRead(ChannelHandlerContext ctx, Object msg) { // (2)
	        // Discard the received data silently.
	        ((ByteBuf) msg).release(); // (3)
	    }
	
	    @Override
	    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
	        // Close the connection when an exception is raised.
	        cause.printStackTrace();
	        ctx.close();
	    }
	}


- `DiscardServerHandler` 继承了 `ChannelInboundHandlerAdapter` ，它是 `ChannelInboundHandler` 的实现类。`ChannelInboundHandler` 提供了各种可以重写的事件处理程序方法。现在，仅仅扩展 `ChannelInboundHandlerAdapter` 就足够了，而不必自己实现处理程序接口。
- 我们在这里重写 `channelRead()` 事件处理程序方法。无论何时从客户端接收到新数据，都会使用接收到的消息调用此方法。在本例中，接收到的消息类型是 `ByteBuf` 。
- 要实现 `Discard` 协议，处理程序必须忽略接收到的消息。`ByteBuf` 是一个引用计数的对象，必须通过 `release()` 方法显式地释放它。请记住，释放传递给处理程序的引用计数对象是处理程序的责任。通常，`channelRead()` 处理程序方法是这样实现的:


		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) {
		    try {
		        // Do something with msg
		    } finally {
		        ReferenceCountUtil.release(msg);
		    }
		}

- `exceptionCaught()` 事件处理程序方法在 `Netty` 由于I/O错误引发异常或处理程序实现在处理事件引发异常时调用。在大多数情况下，应该记录捕获的异常，并在这里关闭它的关联 `Channel`，尽管该方法的实现可能不同，这取决于您希望如何处理异常情况。例如，您可能希望在关闭连接之前发送带有错误代码的响应消息。

到这里，我们的 `Discard Server` 已经完成一半了 ， 我们写一个 `main` 方法来启动这个服务：

	import io.netty.bootstrap.ServerBootstrap;
	
	import io.netty.channel.ChannelFuture;
	import io.netty.channel.ChannelInitializer;
	import io.netty.channel.ChannelOption;
	import io.netty.channel.EventLoopGroup;
	import io.netty.channel.nio.NioEventLoopGroup;
	import io.netty.channel.socket.SocketChannel;
	import io.netty.channel.socket.nio.NioServerSocketChannel;
	    
	/**
	 * Discards any incoming data.
	 */
	public class DiscardServer {
	    
	    private int port;
	    
	    public DiscardServer(int port) {
	        this.port = port;
	    }
	    
	    public void run() throws Exception {
	        EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
	        EventLoopGroup workerGroup = new NioEventLoopGroup();
	        try {
	            ServerBootstrap b = new ServerBootstrap(); // (2)
	            b.group(bossGroup, workerGroup)
	             .channel(NioServerSocketChannel.class) // (3)
	             .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
	                 @Override
	                 public void initChannel(SocketChannel ch) throws Exception {
	                     ch.pipeline().addLast(new DiscardServerHandler());
	                 }
	             })
	             .option(ChannelOption.SO_BACKLOG, 128)          // (5)
	             .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)
	    
	            // Bind and start to accept incoming connections.
	            ChannelFuture f = b.bind(port).sync(); // (7)
	    
	            // Wait until the server socket is closed.
	            // In this example, this does not happen, but you can do that to gracefully
	            // shut down your server.
	            f.channel().closeFuture().sync();
	        } finally {
	            workerGroup.shutdownGracefully();
	            bossGroup.shutdownGracefully();
	        }
	    }
	    
	    public static void main(String[] args) throws Exception {
	        int port = 8080;
	        if (args.length > 0) {
	            port = Integer.parseInt(args[0]);
	        }
	
	        new DiscardServer(port).run();
	    }
	}

- `NioEventLoopGroup` 是一个处理I/O操作的多线程事件循环器。`Netty` 为不同类型的传输方式提供了各种 `EventLoopGroup`实现。我们在这个例子中实现了一个服务器端应用程序，因此将使用两个 `NioEventLoopGroup` 。第一个通常被称为“boss”，接受传入的连接。第二个通常称为“worker”，在 boss 接受连接并将接受的连接注册到 worker 之后，处理接受连接的通信。使用多少线程以及如何将它们映射到创建的通道，取决于 `EventLoopGroup`实现，甚至可以通过构造函数进行配置。

- `ServerBootstrap` 是一个设置服务器的帮助类。您可以直接使用通道设置服务器。但是，请注意这是一个冗长的过程，在大多数情况下您不需要这样做。
 
- 在这里，我们指定使用 `NioServerSocketChannel`类，该类用于实例化一个新通道以接受传入连接。

- 这里指定的处理程序总是由新接受的 `Channel` 来评估。`ChannelInitializer` 是用于帮助用户配置新 `Channel` 的特殊处理程序。您很可能希望通过添加一些处理程序(如 `DiscardServerHandler` )来实现您的网络应用程序，来配置新通道的 `ChannelPipeline`。随着应用程序变得复杂，您可能会向管道中添加更多的处理程序，并最终将这个匿名类提取到顶级类中。

- 您还可以设置特定于通道实现的参数。我们正在编写TCP/IP服务器，因此我们可以设置套接字选项，如 `tcpNoDelay` 和 `keepAlive`。请参阅 `ChannelOption` 的 `apidocs` 和特定的 `ChannelConfig` 实现，以获得受支持的 `ChannelOptions`的概述。

- 你注意到`option()`和 `childOption()`了吗? `option()`用于接收传入连接的 `NioServerSocketChannel`。`childOption()` 用于`parent server`通道接受的通道，在本例中是 `NioServerSocketChannel`。

- 剩下的就是绑定到端口并启动服务器。在这里，我们绑定到机器中所有 `NICs`(网络接口卡)的端口8080。现在，您可以任意次数地调用`bind()`方法(使用不同的绑定地址)。

---

### 查看接收的数据

为了检验我的程序是否正常运行，我们可以用 `Telnet` 尝试向 `8080` 端口发送数据， 检查服务端是否接收到了正确的数据，但是我们目前的程序将任何接收的数据都丢弃了，所以我们需要稍微改动下我们的代码：

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
	    ByteBuf in = (ByteBuf) msg;
	    try {
	        while (in.isReadable()) { // (1)
	            System.out.print((char) in.readByte());
	            System.out.flush();
	        }
	    } finally {
	        ReferenceCountUtil.release(msg); // (2)
	    }
	}

- 可以使用 `System.out.println(in.toString(io.netty.util.CharsetUtil.US_ASCII))` 来代码上面低效的循环，但同时也需要调用 `in.release()`

---

### 编写一个 [`Echo`](https://tools.ietf.org/html/rfc862) 服务

到目前为止 ，我们的应用已经能够接收到数据了， 然而一个服务器，往往不止是接收客户端的数据，还需要相应这些数据，我们这里改写程序，将客户端发送过来的数据，原样发回给客户端：

 	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ctx.write(msg); // (1)
        ctx.flush(); // (2)
    }

- `ChannelHandlerContext` 对象提供各种操作，使您能够触发各种I/O事件和操作。在这里，我们调用 `write(Object)` 以逐字写出接收到的消息。请注意，与 `Discard` 例不同，我们没有释放接收到的消息。这是因为 `Netty` 在它被写入到 wire 时为您释放它。

-` write(Object)`不会将消息直接写入内存。它将数据写入缓存中，然后通过 `ctx.flush()`将其刷到内存。或者，您也可以简洁的调用`ctx.writeAndFlush(msg) `。

### 编写一个 [Time](https://tools.ietf.org/html/rfc868) Server

和前面的例子不同的是，我们不将接收的数据直接返回给客户端，我们这里在客户端连接上的时候，给客户端返回一个代表系统当前时间的一个 32 位的`Integer`。一旦消息发送完成，就将该客户端的连接关闭。在这个例子中，我们将学习如何构建和发送自定义消息给客户端，并在完成时关闭连接。

因为我们要在客户端已连接上的时候给客户端发送消息，所以不用管客户端有没有发送消息过来，我们这里不能再使用 `channelRead()` 方法，重写另一事件监听方法 `channelActive()` ：

	@Override
    public void channelActive(final ChannelHandlerContext ctx) { // (1)
        final ByteBuf time = ctx.alloc().buffer(4); // (2)
        time.writeInt((int) (System.currentTimeMillis() / 1000L + 2208988800L));
        
        final ChannelFuture f = ctx.writeAndFlush(time); // (3)
        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                assert f == future;
                ctx.close();
            }
        }); // (4)
    }

- 如前所述，当建立连接并准备生成流量时，将调用 `channelActive()`方法。让我们编写一个32位整数，它表示该方法中的当前时间。

- 要发送新消息，我们需要分配一个新的缓冲区，其中将包含该消息。我们要写一个32位整数，因此我们需要一个 `Bytebuf`，它的容量至少是4字节。通过 `ChannelHandlerContext.alloc() `获取当前 `ByteBufAllocator`，并分配一个新的缓冲区。

- 像往常一样，我们编写构造的消息。

- 等等，`flip()`在哪里?在NIO中发送消息之前，我们不是经常调用 `java.nio.ByteBuffer.flip()`吗? `ByteBuf`没有这样的方法，因为它有两个指针;

- 一个用于读操作，另一个用于写操作。当您向`ByteBuf`写入内容而阅读器索引没有更改时，写入器索引会增加。`reader`索引和`writer`索引分别表示消息开始和结束的位置。

- 相反，`NIO` 缓冲区没有提供一种清爽的方式来确定消息内容在哪里开始和结束，而无需调用 `flip`方法。当您忘记`flip()`缓冲区时，就会遇到麻烦，因为不会发送任何内容或错误的数据。在`Netty` 中不会发生这样的错误，因为不同的操作类型有不同的指针。你会发现，当你习惯它的时候，它会让你的生活变得更容易 —— 一种没有失控的生活!

- 另一点需要注意的是，`ChannelHandlerContext.write()`(和`writeAndFlush()`)方法返回一个`ChannelFuture`。`ChannelFuture`表示尚未发生的I/O操作。这意味着，可能还没有执行任何请求的操作，因为Netty中的所有操作都是异步的。例如，以下代码可能在发送消息之前关闭连接:

		Channel ch = ...;
		ch.writeAndFlush(message);
		ch.close();


因此，您需要在` ChannelFuture`完成之后调用`close()`方法，该方法是`write()`方法返回的，当写操作完成时，它会通知侦听器。请注意，`close()`也可能不会立即关闭连接，它将返回`ChannelFuture`。

那么，当写请求完成时，我们如何得到通知?这就像在返回的`ChannelFuture`中添加一个`ChannelFutureListener`一样简单。在这里，我们创建了一个新的匿名`ChannelFutureListener`，它在操作完成时关闭通道。
或者，您可以使用预定义的侦听器简化代码:

	f.addListener(ChannelFutureListener.CLOSE);

---

### 编写一个 Time Client

和前面的例子不同的是，我们需要编写一个客户端，因为人类无法理解一个 32 位的数字是不是代表的当前时间。

和服务端最大也是唯一的不同的就是，使用了不同的 `BootStrap` 和 `Channel` 实现类：

	public class TimeClient {
	    public static void main(String[] args) throws Exception {
	        String host = args[0];
	        int port = Integer.parseInt(args[1]);
	        EventLoopGroup workerGroup = new NioEventLoopGroup();
	        
	        try {
	            Bootstrap b = new Bootstrap(); // (1)
	            b.group(workerGroup); // (2)
	            b.channel(NioSocketChannel.class); // (3)
	            b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
	            b.handler(new ChannelInitializer<SocketChannel>() {
	                @Override
	                public void initChannel(SocketChannel ch) throws Exception {
	                    ch.pipeline().addLast(new TimeClientHandler());
	                }
	            });
	            
	            // Start the client.
	            ChannelFuture f = b.connect(host, port).sync(); // (5)
	
	            // Wait until the connection is closed.
	            f.channel().closeFuture().sync();
	        } finally {
	            workerGroup.shutdownGracefully();
	        }
	    }
	}


- `Bootstrap`类似于`ServerBootstrap`，只是它用于非服务器通道，比如客户端通道或无连接通道。如果您只指定一个 `EventLoopGroup`，那么它将作为 boss 组和 worker 组使用。但是boss worker并不用于客户端。

- `NioSocketChannel`用于创建客户端通道，而不是 `NioServerSocketChannel`。注意，这里我们不像在`ServerBootstrap`中那样使用 `childOption()`，因为客户端 `SocketChannel`没有父节点。我们应该调用`connect()`方法，而不是`bind()`方法。

- 正如您所看到的，它与服务器端代码并没有什么不同。那么`ChannelHandler`实现呢?它应该从服务器接收一个32位整数，将其转换为人类可读的格式，打印转换后的时间，并关闭连接:

1 在TCP/IP中，`Netty`读取从对等点发送到 `ByteBuf ` 的数据。

它看起来非常简单，与服务器端示例没有任何区别。但是，这个处理程序有时会抛出`IndexOutOfBoundsException`,我们将在下一节讨论为什么会发生这种情况。

---

### 处理以流为基础的传输协议
##### 关于套接字缓存区的一点小警告

在以流为基础的传输中，如 TCP/IP ,接收的数据存储在套接字接收缓存中，不幸的是，以流传输缓存不是数据包的序列 ，而是字节序列。这就意味着，如果你发送了连个独立的数据包，操作系统不会把这些当成是两条数据，而仅仅是一簇字节序列。因此，就无法保证你接收到的数据和远端发送的是一致的。

##### 第一种解决方案

现在让我们回到 `TimeServer` 中，这里也同样有这个问题，虽然这里传输的数据包很小，被分包的概率也很小，但还是有一定的几率发生，接收到的数据并不足四个字节，

最简单的解决方式是，提供一个内部累积缓存区，当缓存区写满 4 个字节时，再读取缓存区的数据，否则继续监听 `channelRead()` : 

	import java.util.Date;
	
	public class TimeClientHandler extends ChannelInboundHandlerAdapter {
	    private ByteBuf buf;
	    
	    @Override
	    public void handlerAdded(ChannelHandlerContext ctx) {
	        buf = ctx.alloc().buffer(4); // (1)
	    }
	    
	    @Override
	    public void handlerRemoved(ChannelHandlerContext ctx) {
	        buf.release(); // (1)
	        buf = null;
	    }
	    
	    @Override
	    public void channelRead(ChannelHandlerContext ctx, Object msg) {
	        ByteBuf m = (ByteBuf) msg;
	        buf.writeBytes(m); // (2)
	        m.release();
	        
	        if (buf.readableBytes() >= 4) { // (3)
	            long currentTimeMillis = (buf.readUnsignedInt() - 2208988800L) * 1000L;
	            System.out.println(new Date(currentTimeMillis));
	            ctx.close();
	        }
	    }
	    
	    @Override
	    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
	        cause.printStackTrace();
	        ctx.close();
	    }
	}

- `ChannelHandler`有两个生命周期侦听器方法:`handlerAdded()`和`handlerremove()`。只要不阻塞很长时间，就可以执行任意初始化任务。

- 首先，所有接收到的数据都要累积到 buf 中。

- 然后，处理程序必须检查buf是否有足够的数据，在本例中是4字节，并继续实际的业务逻辑。否则，当更多数据到达时，`Netty` 将再次调用 `channelRead()` 方法，最终将累积所有4个字节。

##### 第二种解决方案

虽然第一种方式能解决 Time Server 中的拆包问题，但是代码不够简洁，如果业务变得越来越复杂，如有多个个字段，代码很快会变得不可维护。

正如您可能已经注意到的，您可以向`ChannelPipeline`中添加多个 `ChannelHandler` ，因此，您可以将单个`ChannelHandler` 分割为多个模块处理程序，以降低应用程序的复杂性。例如，您可以将`TimeClientHandler` 分成两个处理程序:

处理碎片问题的时间解码器，和最初简单的 `TimeClientHandler`版本。

幸运的是，Netty提供了一个可扩展的类，它可以帮助您编写第一个开箱即用的类:

	public class TimeDecoder extends ByteToMessageDecoder { // (1)
	    @Override
	    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) { // (2)
	        if (in.readableBytes() < 4) {
	            return; // (3)
	        }
	        
	        out.add(in.readBytes(4)); // (4)
	    }
	}


- 1 `ByteToMessageDecoder`是`ChannelInboundHandler`的一个实现类，它使得处理碎片问题变得更加容易。
- 2 `ByteToMessageDecoder`在接收新数据时使用内部维护的累积缓冲区调用`decode()`方法。
- 3 `decode()`可以决定在累积缓冲区中没有足够数据的地方不添加任何东西。当接收到更多数据时，`ByteToMessageDecoder`将再次调用`decode()`。
- 4 如果 `decode()` 将一个对象添加到out，这意味着解码器成功解码了一条消息。`ByteToMessageDecoder`将丢弃累积缓冲区的读部分。请记住，您不需要解码多个消息。`ByteToMessageDecoder`将一直调用`decode()`方法，直到它没有向out添加任何内容为止。

现在我们有另一个处理程序要插入到 `ChannelPipeline` 中，我们应该在 `TimeClient` 中修改`ChannelInitializer` 实现:

	b.handler(new ChannelInitializer<SocketChannel>() {
	    @Override
	    public void initChannel(SocketChannel ch) throws Exception {
	        ch.pipeline().addLast(new TimeDecoder(), new TimeClientHandler());
	    }
	});

如果你是一个喜欢冒险的人，你可能会想要尝试 `ReplayingDecoder` ，这将进一步简化解码器。不过，您需要参考API参考以获得更多信息。

	public class TimeDecoder extends ReplayingDecoder<Void> {
	    @Override
	    protected void decode(
	            ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
	        out.add(in.readBytes(4));
	    }
	}

此外，Netty还提供了开箱即用的解码器，它使您能够非常容易地实现大多数协议，并帮助您避免以无法维护的整体处理程序实现告终。如需更详细的例子，请参阅以下套件:

- [io.netty.example.factorial](https://netty.io/4.1/xref/io/netty/example/factorial/package-summary.html) for a binary protocol, and
- [io.netty.example.telnet](https://netty.io/4.1/xref/io/netty/example/telnet/package-summary.html) for a text line-based protocol.

---

### 关于 POJO 的讨论

目前为止，我们例子中所有协议消息用到主要的数据结构都是 `ByteBuf` 。这一部分，我们将改善 `Time Client` 和 `Time Server` 例子，我们用 `POJO` 类来代替 `ByteBuf` 

使用 POJO 类的有点显而易见，通过分离从处理程序中提取 `ByteBuf` 信息的代码，您的处理程序变得更加可维护和可重用。在上面 `Time` 协议的例子中，我们只读取一个32位整数，直接使用ByteBuf并不是主要问题。然而，在实现真实世界的协议的时候，你会发现有必须要将他们分开处理。

首先，我们定义一个新的类型 `UnixTime`

	import java.util.Date;
	
	public class UnixTime {
	
	    private final long value;
	    
	    public UnixTime() {
	        this(System.currentTimeMillis() / 1000L + 2208988800L);
	    }
	    
	    public UnixTime(long value) {
	        this.value = value;
	    }
	        
	    public long value() {
	        return value;
	    }
	        
	    @Override
	    public String toString() {
	        return new Date((value() - 2208988800L) * 1000L).toString();
	    }
	}

现在我们可以修改时间解码器来生成 `UnixTime`，而不是 `ByteBuf`。

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
	    if (in.readableBytes() < 4) {
	        return;
	    }
	
	    out.add(new UnixTime(in.readUnsignedInt()));
	}

有了更新后的解码器，`TimeClientHandler` 不再使用 `ByteBuf` 了

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
	    UnixTime m = (UnixTime) msg;
	    System.out.println(m);
	    ctx.close();
	}

是不是更简单更优雅？同样的技巧也可以应用到服务端。让我们首先修改 `TimeServerHandler `:

	@Override
	public void channelActive(ChannelHandlerContext ctx) {
	    ChannelFuture f = ctx.writeAndFlush(new UnixTime());
	    f.addListener(ChannelFutureListener.CLOSE);
	}

现在，仅仅缺一个实现 `ChannelOutboundHandler` 的编码器了，用来将 `UnixTime` 编码成 `ByteBuf`。这比解码简单的多，因为不用考虑分包的问题。

	public class TimeEncoder extends ChannelOutboundHandlerAdapter {
	    @Override
	    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
	        UnixTime m = (UnixTime) msg;
	        ByteBuf encoded = ctx.alloc().buffer(4);
	        encoded.writeInt((int)m.value());
	        ctx.write(encoded, promise); // (1)
	    }
	}

1 在这一行中有很多重要的东西。

- 首先，我们按原样传递原始 `ChannelPromise`，以便Netty在编码数据实际写入到连线时将其标记为成功或失败。
- 其次，我们没有调用 `ctx.flush()`。有一个单独的处理程序方法 `void flush(ChannelHandlerContext ctx)`，用于覆盖 `flush()` 操作。

如果想要变的更简洁，你可以使用 `MessageToByteEncoder`

	public class TimeEncoder extends MessageToByteEncoder<UnixTime> {
	    @Override
	    protected void encode(ChannelHandlerContext ctx, UnixTime msg, ByteBuf out) {
	        out.writeInt((int)msg.value());
	    }
	}

最后一项任务是在 `TimeServerHandler` 之前将 `TimeEncoder` 插入服务器端`ChannelPipeline`，这里当做一个简单的练习。

---

### 关闭你的应用

关闭 `Netty` 应用程序通常与通过 `shutdownGracefully()`关闭的所有 `EventLoopGroups` 一样简单。它返回一个 `Ftrue` ，当 `EventLoopGroup` 已经完全终止并且属于该group的所有`Channel` 已经关闭时，它将通知您。

### 总结

在这一章中，我们快速浏览了 `Netty`，并演示了如何在 `Netty` 之上编写一个完全工作的网络应用程序。
在接下来的章节中有更多关于 `Netty` 的详细信息。我们也鼓励您回顾一下 [iot .net .example](https://github.com/netty/netty/tree/4.1/example/src/main/java/io/netty/example) 包中的 `Netty`示例。
请注意，[社区](https://netty.io/community.html) 一直在等待您的问题和想法，以帮助您并根据您的反馈不断改进Netty及其文档。
