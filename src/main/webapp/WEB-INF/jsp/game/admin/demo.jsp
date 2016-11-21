<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/jsp/commons/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>

    <jsp:include page="/WEB-INF/jsp/game/commons/resources.jsp"/>
    <link rel="stylesheet" href="${ctx}/resources/plugins/materialize/css/ghpages-materialize.css">

    <title>逃离剧本-狂or野-你喜欢哪一个我</title>
    <meta name="description" content="ky's game page">
    <meta name="author" content="kuangye">

</head>
<body>

<header>
    <nav class="top-nav">
        <div class="container">
            <div class="nav-wrapper"><a class="page-title">Chips</a></div>
        </div>
    </nav>

    <jsp:include page="/WEB-INF/jsp/game/admin/leftMenu.jsp"></jsp:include>
    
</header>

<main>
    <div class="container">
    <div class="row">

        <div class="col s12 m9 l10">

            <div id="introduction" class="section scrollspy">
                <h4>Introduction</h4>
                <p class="caption">
                    Chips can be used to represent small blocks of information. They are most commonly used either for contacts or for tags.
                </p>
                <div class="chip">
                    <img src="images/yuna.jpg" alt="Contact Person">
                    Jane Doe
                </div>
                <div class="chip">
                    Tag
                    <i class="close material-icons">close</i>
                </div>
            </div>

            <div id="contact" class="section scrollspy">
                <h4>Contacts</h4>
                <p class="caption">
                    To create a contact chip just add an img inside.
                </p>
                <pre class=" language-markup"><code class=" language-markup">
  <span class="token tag"><span class="token tag"><span class="token punctuation">&lt;</span>div</span> <span class="token attr-name">class</span><span class="token attr-value"><span class="token punctuation">=</span><span class="token punctuation">"</span>chip<span class="token punctuation">"</span></span><span class="token punctuation">&gt;</span></span>
    <span class="token tag"><span class="token tag"><span class="token punctuation">&lt;</span>img</span> <span class="token attr-name">src</span><span class="token attr-value"><span class="token punctuation">=</span><span class="token punctuation">"</span>images/yuna.jpg<span class="token punctuation">"</span></span> <span class="token attr-name">alt</span><span class="token attr-value"><span class="token punctuation">=</span><span class="token punctuation">"</span>Contact Person<span class="token punctuation">"</span></span><span class="token punctuation">&gt;</span></span>
    Jane Doe
  <span class="token tag"><span class="token tag"><span class="token punctuation">&lt;/</span>div</span><span class="token punctuation">&gt;</span></span>
        </code></pre>
            </div>

            <div id="tag" class="section scrollspy">
                <h4>Tags</h4>
                <p class="caption">
                    To create a tag chip just add an close icon inside with the class <code class=" language-markup">close</code>.
                </p>
                <pre class=" language-markup"><code class=" language-markup">
  <span class="token tag"><span class="token tag"><span class="token punctuation">&lt;</span>div</span> <span class="token attr-name">class</span><span class="token attr-value"><span class="token punctuation">=</span><span class="token punctuation">"</span>chip<span class="token punctuation">"</span></span><span class="token punctuation">&gt;</span></span>
    Tag
    <span class="token tag"><span class="token tag"><span class="token punctuation">&lt;</span>i</span> <span class="token attr-name">class</span><span class="token attr-value"><span class="token punctuation">=</span><span class="token punctuation">"</span>close material-icons<span class="token punctuation">"</span></span><span class="token punctuation">&gt;</span></span>close<span class="token tag"><span class="token tag"><span class="token punctuation">&lt;/</span>i</span><span class="token punctuation">&gt;</span></span>
  <span class="token tag"><span class="token tag"><span class="token punctuation">&lt;/</span>div</span><span class="token punctuation">&gt;</span></span>
        </code></pre>
            </div>

            <div id="basic" class="section scrollspy">
                <h4>Javascript Plugin Usage</h4>
                <p class="caption">To add tags, just enter your tag text and press enter. You can delete them by clicking on the close icon or by using your delete button.</p>
                <div class="chips" data-index="0" data-initialized="true"><input id="360c4ce5-5982-d6f9-7967-cc25193d89cf" class="input" placeholder=""></div>
                <p class="caption">Set initial tags.</p>
                <div class="chips chips-initial" data-index="0" data-initialized="true"><div class="chip">Apple<i class="material-icons close">close</i></div><div class="chip">Microsoft<i class="material-icons close">close</i></div><div class="chip">Google<i class="material-icons close">close</i></div><input id="270e08e2-a229-f26e-914d-495c81d034fb" class="input" placeholder=""></div>
                <p class="caption">Use placeholders and override hint texts.</p>
                <div class="chips chips-placeholder" data-index="0" data-initialized="true"><input id="d00f8058-0448-7999-28d7-3f10a28f4300" class="input" placeholder="+Tag"></div>
            </div>

            <div id="properties" class="section scrollspy">
                <h4>Markup</h4>
                <pre class=" language-markup"><code class=" language-markup">
  <span class="token tag"><span class="token tag"><span class="token punctuation">&lt;</span>div</span> <span class="token attr-name">class</span><span class="token attr-value"><span class="token punctuation">=</span><span class="token punctuation">"</span>chips<span class="token punctuation">"</span></span><span class="token punctuation">&gt;</span></span><span class="token tag"><span class="token tag"><span class="token punctuation">&lt;/</span>div</span><span class="token punctuation">&gt;</span></span>
  <span class="token tag"><span class="token tag"><span class="token punctuation">&lt;</span>div</span> <span class="token attr-name">class</span><span class="token attr-value"><span class="token punctuation">=</span><span class="token punctuation">"</span>chips chips-initial<span class="token punctuation">"</span></span><span class="token punctuation">&gt;</span></span><span class="token tag"><span class="token tag"><span class="token punctuation">&lt;/</span>div</span><span class="token punctuation">&gt;</span></span>
  <span class="token tag"><span class="token tag"><span class="token punctuation">&lt;</span>div</span> <span class="token attr-name">class</span><span class="token attr-value"><span class="token punctuation">=</span><span class="token punctuation">"</span>chips chips-placeholder<span class="token punctuation">"</span></span><span class="token punctuation">&gt;</span></span><span class="token tag"><span class="token tag"><span class="token punctuation">&lt;/</span>div</span><span class="token punctuation">&gt;</span></span>
        </code></pre>
                <h4>jQuery Initialization</h4>
                <pre class=" language-javascript"><code class=" language-javascript">
  <span class="token function">$</span><span class="token punctuation">(</span><span class="token string">'.chips'</span><span class="token punctuation">)</span><span class="token punctuation">.</span><span class="token function">material_chip</span><span class="token punctuation">(</span><span class="token punctuation">)</span><span class="token punctuation">;</span>
  <span class="token function">$</span><span class="token punctuation">(</span><span class="token string">'.chips-initial'</span><span class="token punctuation">)</span><span class="token punctuation">.</span><span class="token function">material_chip</span><span class="token punctuation">(</span><span class="token punctuation">{</span>
    data<span class="token punctuation">:</span> <span class="token punctuation">[</span><span class="token punctuation">{</span>
      tag<span class="token punctuation">:</span> <span class="token string">'Apple'</span><span class="token punctuation">,</span>
    <span class="token punctuation">}</span><span class="token punctuation">,</span> <span class="token punctuation">{</span>
      tag<span class="token punctuation">:</span> <span class="token string">'Microsoft'</span><span class="token punctuation">,</span>
    <span class="token punctuation">}</span><span class="token punctuation">,</span> <span class="token punctuation">{</span>
      tag<span class="token punctuation">:</span> <span class="token string">'Google'</span><span class="token punctuation">,</span>
    <span class="token punctuation">}</span><span class="token punctuation">]</span><span class="token punctuation">,</span>
  <span class="token punctuation">}</span><span class="token punctuation">)</span><span class="token punctuation">;</span>
  <span class="token function">$</span><span class="token punctuation">(</span><span class="token string">'.chips-placeholder'</span><span class="token punctuation">)</span><span class="token punctuation">.</span><span class="token function">material_chip</span><span class="token punctuation">(</span><span class="token punctuation">{</span>
    placeholder<span class="token punctuation">:</span> <span class="token string">'Enter a tag'</span><span class="token punctuation">,</span>
    secondaryPlaceholder<span class="token punctuation">:</span> <span class="token string">'+Tag'</span><span class="token punctuation">,</span>
  <span class="token punctuation">}</span><span class="token punctuation">)</span><span class="token punctuation">;</span>
        </code></pre>
                <p class="caption">Chip data object</p>
                <pre class=" language-javascript"><code class=" language-javascript">
  <span class="token keyword">var</span> chip <span class="token operator">=</span> <span class="token punctuation">{</span>
    tag<span class="token punctuation">:</span> <span class="token string">'chip content'</span><span class="token punctuation">,</span>
    image<span class="token punctuation">:</span> <span class="token string">''</span><span class="token punctuation">,</span> <span class="token comment" spellcheck="true">//optional
</span>    id<span class="token punctuation">:</span> <span class="token number">1</span><span class="token punctuation">,</span> <span class="token comment" spellcheck="true">//optional
</span>  <span class="token punctuation">}</span><span class="token punctuation">;</span>
        </code></pre>

                <br>
                <h4>jQuery Plugin Options</h4>
                <table class="table highlight">
                    <thead>
                    <tr>
                        <th>Option Name</th>
                        <th>Type</th>
                        <th>Description</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                        <td>data</td>
                        <td>array</td>
                        <td>Set the chip data (look at the Chip data object)</td>
                    </tr>
                    <tr>
                        <td>placeholder</td>
                        <td>string</td>
                        <td>Set first placeholder when there are no tags.</td>
                    </tr>
                    <tr>
                        <td>secondaryPlaceholder</td>
                        <td>string</td>
                        <td>Set second placeholder when adding additional tags.</td>
                    </tr>
                    <tr>
                    </tr></tbody>
                </table>
            </div>

            <div id="events" class="section scrollspy">
                <h4>Events</h4>
                <p class="caption">Material chips exposes a few events for hooking
                    into chips functionality.
                </p>
                <table class="table highlight">
                    <thead>
                    <tr>
                        <th>Event</th>
                        <th>Description</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                        <td>chips.add</td>
                        <td>this method is triggered when a chip is <strong>added</strong>.</td>
                    </tr>
                    <tr>
                        <td>chips.delete</td>
                        <td>this method is triggered when a chip is <strong>deleted</strong>.</td>
                    </tr>
                    <tr>
                        <td>chips.select</td>
                        <td>this method is triggered when a chip is <strong>selected</strong>.</td>
                    </tr>
                    </tbody>
                </table>

                <br><br>

                <pre class=" language-javascript"><code class=" language-javascript">
  <span class="token function">$</span><span class="token punctuation">(</span><span class="token string">'.chips'</span><span class="token punctuation">)</span><span class="token punctuation">.</span><span class="token function">on</span><span class="token punctuation">(</span><span class="token string">'chip.add'</span><span class="token punctuation">,</span> <span class="token keyword">function</span><span class="token punctuation">(</span>e<span class="token punctuation">,</span> chip<span class="token punctuation">)</span><span class="token punctuation">{</span>
    <span class="token comment" spellcheck="true">// you have the added chip here
</span>  <span class="token punctuation">}</span><span class="token punctuation">)</span><span class="token punctuation">;</span>

  <span class="token function">$</span><span class="token punctuation">(</span><span class="token string">'.chips'</span><span class="token punctuation">)</span><span class="token punctuation">.</span><span class="token function">on</span><span class="token punctuation">(</span><span class="token string">'chip.delete'</span><span class="token punctuation">,</span> <span class="token keyword">function</span><span class="token punctuation">(</span>e<span class="token punctuation">,</span> chip<span class="token punctuation">)</span><span class="token punctuation">{</span>
    <span class="token comment" spellcheck="true">// you have the deleted chip here
</span>  <span class="token punctuation">}</span><span class="token punctuation">)</span><span class="token punctuation">;</span>

  <span class="token function">$</span><span class="token punctuation">(</span><span class="token string">'.chips'</span><span class="token punctuation">)</span><span class="token punctuation">.</span><span class="token function">on</span><span class="token punctuation">(</span><span class="token string">'chip.select'</span><span class="token punctuation">,</span> <span class="token keyword">function</span><span class="token punctuation">(</span>e<span class="token punctuation">,</span> chip<span class="token punctuation">)</span><span class="token punctuation">{</span>
    <span class="token comment" spellcheck="true">// you have the selected chip here
</span>  <span class="token punctuation">}</span><span class="token punctuation">)</span><span class="token punctuation">;</span>



        </code></pre>

            </div>
            <div id="methods" class="section scrollspy">
                <h4>Methods</h4>
                <p class="caption">
                    Use these methods to access the chip data.
                </p>
                <table class="table highlight">
                    <thead>
                    <tr>
                        <th>Parameter</th>
                        <th>Description</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                        <td>data</td>
                        <td>It returns the stored data.</td>
                    </tr>
                    </tbody>
                </table>

                <br><br>

                <pre class=" language-javascript"><code class=" language-javascript">
  <span class="token function">$</span><span class="token punctuation">(</span><span class="token string">'.chips-initial'</span><span class="token punctuation">)</span><span class="token punctuation">.</span><span class="token function">material_chip</span><span class="token punctuation">(</span><span class="token string">'data'</span><span class="token punctuation">)</span><span class="token punctuation">;</span>
        </code></pre>


            </div>
        </div>

        <!-- Table of Contents -->
        <div class="col hide-on-small-only m3 l2">
            <div class="toc-wrapper pinned" style="top: 0px;">
                <div style="height: 1px;">
                    <ul class="section table-of-contents">
                        <li><a href="#contact" class="">Contacts</a></li>
                        <li><a href="#tag" class="">Tags</a></li>
                        <li><a href="#basic" class="">Plugin</a></li>
                        <li><a href="#properties" class="">Options</a></li>
                        <li><a href="#events">Events</a></li>
                        <li><a href="#methods">Methods</a></li>
                    </ul>
                </div>
            </div>
        </div>

    </div>
</div>
</main>   


<jsp:include page="/WEB-INF/jsp/game/admin/footer.jsp"></jsp:include>

<!--  Scripts-->
<!-- 语法高亮 -->
<script src="${ctx}/resources/plugins/prism/prism.js"></script>
<!-- 搜索索引 -->
<script src="${ctx}/resources/plugins/jade/lunr.min.js"></script>
<script src="${ctx}/resources/plugins/jade/search.js"></script>

<script src="${ctx}/resources/js/game/init.js"></script>

</body>
</html>
